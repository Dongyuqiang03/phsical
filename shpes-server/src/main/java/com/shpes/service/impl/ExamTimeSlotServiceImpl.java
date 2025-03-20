package com.shpes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shpes.common.api.CommonPage;
import com.shpes.common.enums.ResultCode;
import com.shpes.common.exception.ApiException;
import com.shpes.entity.ExamTimeSlot;
import com.shpes.mapper.ExamTimeSlotMapper;
import com.shpes.service.ExamTimeSlotService;
import com.shpes.vo.ExamTimeSlotVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 体检时间段服务实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ExamTimeSlotServiceImpl extends ServiceImpl<ExamTimeSlotMapper, ExamTimeSlot> implements ExamTimeSlotService {

    @Override
    public ExamTimeSlotVO getTimeSlot(Long id) {
        ExamTimeSlot timeSlot = getById(id);
        if (timeSlot == null) {
            throw new ApiException(ResultCode.TIME_SLOT_NOT_EXIST);
        }
        return convertToVO(timeSlot);
    }

    @Override
    public CommonPage<ExamTimeSlotVO> getTimeSlotPage(Integer pageNum, Integer pageSize, Long departmentId, LocalDate date) {
        LambdaQueryWrapper<ExamTimeSlot> wrapper = new LambdaQueryWrapper<>();
        if (departmentId != null) {
            wrapper.eq(ExamTimeSlot::getDepartmentId, departmentId);
        }
        if (date != null) {
            wrapper.eq(ExamTimeSlot::getDate, date);
        }
        wrapper.orderByAsc(ExamTimeSlot::getStartTime);

        Page<ExamTimeSlot> page = page(new Page<>(pageNum, pageSize), wrapper);
        List<ExamTimeSlotVO> records = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return CommonPage.restPage(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<ExamTimeSlotVO> getAvailableTimeSlots(Long departmentId, LocalDate date) {
        LambdaQueryWrapper<ExamTimeSlot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamTimeSlot::getDepartmentId, departmentId)
                .eq(ExamTimeSlot::getDate, date)
                .eq(ExamTimeSlot::getStatus, 1) // 1-可用
                .apply("booked_count < capacity") // 未满
                .orderByAsc(ExamTimeSlot::getStartTime);

        return list(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public void createTimeSlot(ExamTimeSlot timeSlot) {
        // 检查时间段是否已存在
        LambdaQueryWrapper<ExamTimeSlot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamTimeSlot::getDepartmentId, timeSlot.getDepartmentId())
                .eq(ExamTimeSlot::getDate, timeSlot.getDate())
                .eq(ExamTimeSlot::getStartTime, timeSlot.getStartTime())
                .eq(ExamTimeSlot::getEndTime, timeSlot.getEndTime());
        if (count(wrapper) > 0) {
            throw new ApiException(ResultCode.TIME_SLOT_EXIST);
        }

        timeSlot.setBookedCount(0);
        timeSlot.setStatus(1); // 1-可用
        save(timeSlot);
    }

    @Override
    public void updateTimeSlot(ExamTimeSlot timeSlot) {
        ExamTimeSlot existingSlot = getById(timeSlot.getId());
        if (existingSlot == null) {
            throw new ApiException(ResultCode.TIME_SLOT_NOT_EXIST);
        }

        // 检查时间段是否已被预约
        if (existingSlot.getBookedCount() > 0) {
            throw new ApiException(ResultCode.TIME_SLOT_BOOKED);
        }

        // 检查时间段是否已存在
        LambdaQueryWrapper<ExamTimeSlot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamTimeSlot::getDepartmentId, timeSlot.getDepartmentId())
                .eq(ExamTimeSlot::getDate, timeSlot.getDate())
                .eq(ExamTimeSlot::getStartTime, timeSlot.getStartTime())
                .eq(ExamTimeSlot::getEndTime, timeSlot.getEndTime())
                .ne(ExamTimeSlot::getId, timeSlot.getId());
        if (count(wrapper) > 0) {
            throw new ApiException(ResultCode.TIME_SLOT_EXIST);
        }

        updateById(timeSlot);
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
    public void updateStatus(Long id, Integer status) {
        ExamTimeSlot timeSlot = getById(id);
        if (timeSlot == null) {
            throw new ApiException(ResultCode.TIME_SLOT_NOT_EXIST);
        }

        // 检查时间段是否已被预约
        if (timeSlot.getBookedCount() > 0) {
            throw new ApiException(ResultCode.TIME_SLOT_BOOKED);
        }

        timeSlot.setStatus(status);
        updateById(timeSlot);
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

    private ExamTimeSlotVO convertToVO(ExamTimeSlot timeSlot) {
        if (timeSlot == null) {
            return null;
        }
        ExamTimeSlotVO vo = new ExamTimeSlotVO();
        BeanUtils.copyProperties(timeSlot, vo);
        return vo;
    }
}