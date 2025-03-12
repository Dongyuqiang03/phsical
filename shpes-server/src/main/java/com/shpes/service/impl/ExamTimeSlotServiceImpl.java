package com.shpes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shpes.common.api.CommonPage;
import com.shpes.common.api.ResultCode;
import com.shpes.common.exception.ApiException;
import com.shpes.entity.ExamTimeSlot;
import com.shpes.mapper.ExamTimeSlotMapper;
import com.shpes.service.ExamTimeSlotService;
import com.shpes.service.SysDepartmentService;
import com.shpes.vo.ExamTimeSlotVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 体检时间段服务实现类
 */
@Service
public class ExamTimeSlotServiceImpl extends ServiceImpl<ExamTimeSlotMapper, ExamTimeSlot> implements ExamTimeSlotService {

    @Autowired
    private SysDepartmentService departmentService;

    @Override
    public CommonPage<ExamTimeSlotVO> getTimeSlotPage(Integer pageNum, Integer pageSize, Long departmentId, LocalDate date) {
        LambdaQueryWrapper<ExamTimeSlot> wrapper = new LambdaQueryWrapper<>();
        if (departmentId != null) {
            wrapper.eq(ExamTimeSlot::getDepartmentId, departmentId);
        }
        if (date != null) {
            wrapper.eq(ExamTimeSlot::getDate, date);
        }
        wrapper.orderByAsc(ExamTimeSlot::getDate)
                .orderByAsc(ExamTimeSlot::getStartTime);
        
        Page<ExamTimeSlot> page = page(new Page<>(pageNum, pageSize), wrapper);
        List<ExamTimeSlotVO> records = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        Page<ExamTimeSlotVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(records);
                
        return CommonPage.restPage(voPage);
    }

    @Override
    public List<ExamTimeSlotVO> getAvailableTimeSlots(Long departmentId, LocalDate date) {
        LambdaQueryWrapper<ExamTimeSlot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamTimeSlot::getDepartmentId, departmentId)
                .eq(ExamTimeSlot::getDate, date)
                .eq(ExamTimeSlot::getStatus, 1)
                .apply("booked_count < capacity")
                .orderByAsc(ExamTimeSlot::getStartTime);
        return list(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public ExamTimeSlotVO getTimeSlot(Long id) {
        return convertToVO(getById(id));
    }

    @Override
    public ExamTimeSlotVO createTimeSlot(ExamTimeSlot timeSlot) {
        timeSlot.setBookedCount(0);
        save(timeSlot);
        return convertToVO(timeSlot);
    }

    @Override
    public ExamTimeSlotVO updateTimeSlot(ExamTimeSlot timeSlot) {
        if (!updateById(timeSlot)) {
            throw new ApiException(ResultCode.TIME_SLOT_NOT_EXIST);
        }
        return convertToVO(timeSlot);
    }

    @Override
    public void deleteTimeSlot(Long id) {
        if (!removeById(id)) {
            throw new ApiException(ResultCode.TIME_SLOT_NOT_EXIST);
        }
    }

    @Override
    public ExamTimeSlotVO updateStatus(Long id, Integer status) {
        ExamTimeSlot timeSlot = getById(id);
        if (timeSlot == null) {
            throw new ApiException(ResultCode.TIME_SLOT_NOT_EXIST);
        }
        timeSlot.setStatus(status);
        updateById(timeSlot);
        return convertToVO(timeSlot);
    }

    @Override
    public void incrementBookedCount(Long id) {
        LambdaUpdateWrapper<ExamTimeSlot> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ExamTimeSlot::getId, id)
                .apply("booked_count < capacity")
                .setSql("booked_count = booked_count + 1");
        update(null, wrapper);
    }

    @Override
    public void decrementBookedCount(Long id) {
        LambdaUpdateWrapper<ExamTimeSlot> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ExamTimeSlot::getId, id)
                .gt(ExamTimeSlot::getBookedCount, 0)
                .setSql("booked_count = booked_count - 1");
        update(null, wrapper);
    }

    /**
     * 将实体转换为VO
     */
    private ExamTimeSlotVO convertToVO(ExamTimeSlot timeSlot) {
        if (timeSlot == null) {
            return null;
        }
        ExamTimeSlotVO vo = new ExamTimeSlotVO();
        BeanUtils.copyProperties(timeSlot, vo);
        
        // 设置部门名称
        if (timeSlot.getDepartmentId() != null) {
            vo.setDepartmentName(departmentService.getDepartmentNameById(timeSlot.getDepartmentId()));
        }
        
        return vo;
    }
}