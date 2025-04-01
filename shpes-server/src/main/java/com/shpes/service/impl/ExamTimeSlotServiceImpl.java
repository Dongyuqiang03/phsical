package com.shpes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shpes.common.api.CommonPage;
import com.shpes.common.enums.ResultCode;
import com.shpes.common.exception.ApiException;
import com.shpes.dto.ExamTimeSlotDTO;
import com.shpes.dto.ExamTimeSlotBatchDTO;
import com.shpes.dto.ExamTimeSlotUpdateDTO;
import com.shpes.entity.ExamTimeSlot;
import com.shpes.mapper.ExamTimeSlotMapper;
import com.shpes.service.ExamTimeSlotService;
import com.shpes.service.SysDepartmentService;
import com.shpes.vo.ExamTimeSlotVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 体检时间段服务实现类
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ExamTimeSlotServiceImpl extends ServiceImpl<ExamTimeSlotMapper, ExamTimeSlot> implements ExamTimeSlotService {

    @Autowired
    private SysDepartmentService sysDepartmentService;

    @Override
    public ExamTimeSlotVO getTimeSlot(Long id) {
        ExamTimeSlot timeSlot = getById(id);
        if (timeSlot == null) {
            throw new ApiException(ResultCode.TIME_SLOT_NOT_EXIST);
        }
        return convertToVO(timeSlot);
    }

    @Override
    public CommonPage<ExamTimeSlotVO> getTimeSlotPage(Integer pageNum, Integer pageSize, Long deptId, LocalDate date) {
        LambdaQueryWrapper<ExamTimeSlot> wrapper = new LambdaQueryWrapper<>();
        if (deptId != null) {
            wrapper.eq(ExamTimeSlot::getDeptId, deptId);
        }
        if (date != null) {
            wrapper.eq(ExamTimeSlot::getDate, date);
        }
        wrapper.orderByAsc(ExamTimeSlot::getDate, ExamTimeSlot::getStartTime);

        Page<ExamTimeSlot> page = page(new Page<>(pageNum, pageSize), wrapper);
        List<ExamTimeSlotVO> records = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return CommonPage.restPage(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<ExamTimeSlotVO> getAvailableTimeSlots(Long deptId, LocalDate date) {
        LambdaQueryWrapper<ExamTimeSlot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamTimeSlot::getDeptId, deptId)
                .eq(ExamTimeSlot::getDate, date)
                .eq(ExamTimeSlot::getStatus, 1) // 1-可用
                .apply("booked_count < capacity") // 未满
                .orderByAsc(ExamTimeSlot::getStartTime);

        return list(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public void createTimeSlot(ExamTimeSlotDTO timeSlotDTO) {
        // 检查时间段是否已存在
        LambdaQueryWrapper<ExamTimeSlot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamTimeSlot::getDeptId, timeSlotDTO.getDeptId())
                .eq(ExamTimeSlot::getDate, timeSlotDTO.getDate())
                .eq(ExamTimeSlot::getStartTime, timeSlotDTO.getStartTime())
                .eq(ExamTimeSlot::getEndTime, timeSlotDTO.getEndTime());
        if (count(wrapper) > 0) {
            throw new ApiException(ResultCode.TIME_SLOT_EXIST);
        }

        ExamTimeSlot timeSlot = new ExamTimeSlot();
        BeanUtils.copyProperties(timeSlotDTO, timeSlot);
        timeSlot.setBookedCount(0);
        if (timeSlot.getStatus() == null) {
            timeSlot.setStatus(1); // 1-可用，默认启用
        }
        save(timeSlot);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchCreateTimeSlot(ExamTimeSlotBatchDTO batchDTO) {
        LocalDate startDate = batchDTO.getStartDate();
        LocalDate endDate = batchDTO.getEndDate();
        List<LocalDate> excludeDates = batchDTO.getExcludeDates() != null ? batchDTO.getExcludeDates() : new ArrayList<>();
        
        if (startDate.isAfter(endDate)) {
            throw new ApiException(ResultCode.PARAM_ERROR, "开始日期不能晚于结束日期");
        }
        
        List<ExamTimeSlot> timeSlots = new ArrayList<>();
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // 跳过排除的日期
            if (excludeDates.contains(date)) {
                continue;
            }
            
            // 如果需要跳过周末，可以在这里添加逻辑
            // if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            //     continue;
            // }
            
            ExamTimeSlot timeSlot = new ExamTimeSlot();
            timeSlot.setDeptId(batchDTO.getDeptId());
            timeSlot.setDate(date);
            timeSlot.setStartTime(batchDTO.getStartTime());
            timeSlot.setEndTime(batchDTO.getEndTime());
            timeSlot.setCapacity(batchDTO.getCapacity());
            timeSlot.setStatus(batchDTO.getStatus() != null ? batchDTO.getStatus() : 1);
            timeSlot.setRemark(batchDTO.getRemark());
            timeSlot.setBookedCount(0);
            
            // 检查是否已存在相同时间段
            LambdaQueryWrapper<ExamTimeSlot> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ExamTimeSlot::getDeptId, timeSlot.getDeptId())
                    .eq(ExamTimeSlot::getDate, timeSlot.getDate())
                    .eq(ExamTimeSlot::getStartTime, timeSlot.getStartTime())
                    .eq(ExamTimeSlot::getEndTime, timeSlot.getEndTime());
            
            if (count(wrapper) == 0) {
                timeSlots.add(timeSlot);
            }
        }
        
        if (!timeSlots.isEmpty()) {
            saveBatch(timeSlots);
        }
    }

    @Override
    public void updateTimeSlot(Long id, ExamTimeSlotUpdateDTO updateDTO) {
        ExamTimeSlot existingSlot = getById(id);
        if (existingSlot == null) {
            throw new ApiException(ResultCode.TIME_SLOT_NOT_EXIST);
        }

        // 创建更新实体
        ExamTimeSlot updateEntity = new ExamTimeSlot();
        updateEntity.setId(id);
        
        // 检查是否包含重要字段更新
        boolean hasKeyFieldUpdates = updateDTO.getDate() != null 
                || updateDTO.getStartTime() != null 
                || updateDTO.getEndTime() != null 
                || updateDTO.getDeptId() != null;
        
        // 如果包含重要字段更新且已有预约，则拒绝更新
        if (hasKeyFieldUpdates && existingSlot.getBookedCount() > 0) {
            throw new ApiException(ResultCode.TIME_SLOT_BOOKED, "该时间段已有预约，无法修改重要信息");
        }
        
        // 容量更新
        if (updateDTO.getCapacity() != null) {
            // 检查新容量是否小于已预约数
            if (updateDTO.getCapacity() < existingSlot.getBookedCount()) {
                throw new ApiException(ResultCode.PARAM_ERROR, "新容量不能小于已预约数量");
            }
            updateEntity.setCapacity(updateDTO.getCapacity());
        }
        
        // 状态更新
        if (updateDTO.getStatus() != null) {
            // 如果要禁用，检查时间段是否已被预约
            if (updateDTO.getStatus() == 0 && existingSlot.getBookedCount() > 0) {
                throw new ApiException(ResultCode.TIME_SLOT_BOOKED, "该时间段已有预约，无法禁用");
            }
            updateEntity.setStatus(updateDTO.getStatus());
        }
        
        // 更新备注
        if (updateDTO.getRemark() != null) {
            updateEntity.setRemark(updateDTO.getRemark());
        }
        
        // 更新日期
        if (updateDTO.getDate() != null) {
            updateEntity.setDate(updateDTO.getDate());
        }
        
        // 更新开始时间
        if (updateDTO.getStartTime() != null) {
            updateEntity.setStartTime(updateDTO.getStartTime());
        }
        
        // 更新结束时间
        if (updateDTO.getEndTime() != null) {
            updateEntity.setEndTime(updateDTO.getEndTime());
        }
        
        // 更新科室ID
        if (updateDTO.getDeptId() != null) {
            updateEntity.setDeptId(updateDTO.getDeptId());
        }
        
        // 如果有重要字段更新，需要检查是否与其他时间段冲突
        if (hasKeyFieldUpdates) {
            // 准备检查冲突所需的字段
            LocalDate date = updateDTO.getDate() != null ? updateDTO.getDate() : existingSlot.getDate();
            LocalTime startTime = updateDTO.getStartTime() != null ? updateDTO.getStartTime() : existingSlot.getStartTime();
            LocalTime endTime = updateDTO.getEndTime() != null ? updateDTO.getEndTime() : existingSlot.getEndTime();
            Long deptId = updateDTO.getDeptId() != null ? updateDTO.getDeptId() : existingSlot.getDeptId();
            
            // 检查是否存在冲突
            LambdaQueryWrapper<ExamTimeSlot> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ExamTimeSlot::getDeptId, deptId)
                    .eq(ExamTimeSlot::getDate, date)
                    .eq(ExamTimeSlot::getStartTime, startTime)
                    .eq(ExamTimeSlot::getEndTime, endTime)
                    .ne(ExamTimeSlot::getId, id);
            
            if (count(wrapper) > 0) {
                throw new ApiException(ResultCode.TIME_SLOT_EXIST, "时间段已存在");
            }
        }
        
        // 保留原来的预约数
        updateEntity.setBookedCount(existingSlot.getBookedCount());
        
        // 执行更新
        updateById(updateEntity);
    }

    @Override
    public void deleteTimeSlot(Long id) {
        ExamTimeSlot timeSlot = getById(id);
        if (timeSlot == null) {
            throw new ApiException(ResultCode.TIME_SLOT_NOT_EXIST);
        }

        // 检查时间段是否已被预约
        if (timeSlot.getBookedCount() > 0) {
            throw new ApiException(ResultCode.TIME_SLOT_BOOKED);
        }

        removeById(id);
    }

    @Override
    public void incrementBookedCount(Long id) {
        ExamTimeSlot timeSlot = getById(id);
        if (timeSlot == null) {
            throw new ApiException(ResultCode.TIME_SLOT_NOT_EXIST);
        }

        if (timeSlot.getBookedCount() >= timeSlot.getCapacity()) {
            throw new ApiException(ResultCode.TIME_SLOT_FULL);
        }

        timeSlot.setBookedCount(timeSlot.getBookedCount() + 1);
        updateById(timeSlot);
    }

    @Override
    public void decrementBookedCount(Long id) {
        ExamTimeSlot timeSlot = getById(id);
        if (timeSlot == null) {
            throw new ApiException(ResultCode.TIME_SLOT_NOT_EXIST);
        }

        if (timeSlot.getBookedCount() <= 0) {
            throw new ApiException(ResultCode.TIME_SLOT_EMPTY);
        }

        timeSlot.setBookedCount(timeSlot.getBookedCount() - 1);
        updateById(timeSlot);
    }

    @Override
    public List<ExamTimeSlotVO> getAvailableTimeSlotsByDate(LocalDate date) {
        LambdaQueryWrapper<ExamTimeSlot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamTimeSlot::getDate, date)
               .eq(ExamTimeSlot::getStatus, 1) // 1-可用
               .apply("booked_count < capacity") // 未满
               .orderByAsc(ExamTimeSlot::getStartTime);

        return list(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean hasAvailableTimeSlots(Long packageId, LocalDate date) {
        // 本方法检查指定日期是否有对应套餐的可用时间段
        // 如果不依赖科室，直接检查该日期是否有任何可用时间段
        LambdaQueryWrapper<ExamTimeSlot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamTimeSlot::getDate, date)
               .eq(ExamTimeSlot::getStatus, 1)
               .apply("booked_count < capacity")
               .last("LIMIT 1"); // 只需要确认是否存在，返回一条记录即可
        
        return count(wrapper) > 0;
        
        // 如果未来业务需要，可以增加对套餐特殊要求的判断
        // 例如某些特殊套餐只能在特定科室预约
    }

    private ExamTimeSlotVO convertToVO(ExamTimeSlot timeSlot) {
        if (timeSlot == null) {
            return null;
        }
        ExamTimeSlotVO vo = new ExamTimeSlotVO();
        BeanUtils.copyProperties(timeSlot, vo);
        vo.setDeptId(timeSlot.getDeptId());
        
        // 获取科室名称
        if (timeSlot.getDeptId() != null) {
            try {
                // 调用部门服务获取科室名称
                String deptName = sysDepartmentService.getDepartmentNameById(timeSlot.getDeptId());
                vo.setDeptName(deptName);
            } catch (Exception e) {
                // 记录错误但不中断流程
                log.error("Failed to get department name for id: {}", timeSlot.getDeptId(), e);
            }
        }
        
        vo.setAppointmentCount(timeSlot.getBookedCount());
        vo.setRemainingCapacity(timeSlot.getCapacity() - timeSlot.getBookedCount());
        return vo;
    }
}