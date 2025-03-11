package com.shpes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shpes.entity.ExamTimeSlot;
import com.shpes.mapper.ExamTimeSlotMapper;
import com.shpes.service.ExamTimeSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 体检时间段服务实现类
 */
@Service
public class ExamTimeSlotServiceImpl implements ExamTimeSlotService {

    @Autowired
    private ExamTimeSlotMapper timeSlotMapper;

    @Override
    public Page<ExamTimeSlot> getTimeSlotPage(Integer pageNum, Integer pageSize, Long departmentId, LocalDate date) {
        LambdaQueryWrapper<ExamTimeSlot> wrapper = new LambdaQueryWrapper<>();
        if (departmentId != null) {
            wrapper.eq(ExamTimeSlot::getDepartmentId, departmentId);
        }
        if (date != null) {
            wrapper.eq(ExamTimeSlot::getDate, date);
        }
        wrapper.orderByAsc(ExamTimeSlot::getDate)
                .orderByAsc(ExamTimeSlot::getStartTime);
        return timeSlotMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<ExamTimeSlot> getAvailableTimeSlots(Long departmentId, LocalDate date) {
        LambdaQueryWrapper<ExamTimeSlot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamTimeSlot::getDepartmentId, departmentId)
                .eq(ExamTimeSlot::getDate, date)
                .eq(ExamTimeSlot::getStatus, 1)
                .lt(ExamTimeSlot::getBookedCount, ExamTimeSlot::getCapacity)
                .orderByAsc(ExamTimeSlot::getStartTime);
        return timeSlotMapper.selectList(wrapper);
    }

    @Override
    public ExamTimeSlot getTimeSlot(Long id) {
        return timeSlotMapper.selectById(id);
    }

    @Override
    public void createTimeSlot(ExamTimeSlot timeSlot) {
        timeSlot.setBookedCount(0);
        timeSlotMapper.insert(timeSlot);
    }

    @Override
    public void updateTimeSlot(ExamTimeSlot timeSlot) {
        timeSlotMapper.updateById(timeSlot);
    }

    @Override
    public void deleteTimeSlot(Long id) {
        timeSlotMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        ExamTimeSlot timeSlot = new ExamTimeSlot();
        timeSlot.setId(id);
        timeSlot.setStatus(status);
        timeSlotMapper.updateById(timeSlot);
    }

    @Override
    public void incrementBookedCount(Long id) {
        LambdaUpdateWrapper<ExamTimeSlot> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ExamTimeSlot::getId, id)
                .lt(ExamTimeSlot::getBookedCount, ExamTimeSlot::getCapacity)
                .setSql("booked_count = booked_count + 1");
        timeSlotMapper.update(null, wrapper);
    }

    @Override
    public void decrementBookedCount(Long id) {
        LambdaUpdateWrapper<ExamTimeSlot> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ExamTimeSlot::getId, id)
                .gt(ExamTimeSlot::getBookedCount, 0)
                .setSql("booked_count = booked_count - 1");
        timeSlotMapper.update(null, wrapper);
    }
} 