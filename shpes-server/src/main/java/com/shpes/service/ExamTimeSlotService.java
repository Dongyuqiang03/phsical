package com.shpes.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shpes.entity.ExamTimeSlot;

import java.time.LocalDate;
import java.util.List;

/**
 * 体检时间段服务接口
 */
public interface ExamTimeSlotService {

    /**
     * 分页查询时间段
     */
    Page<ExamTimeSlot> getTimeSlotPage(Integer pageNum, Integer pageSize, Long departmentId, LocalDate date);

    /**
     * 获取可预约时间段
     */
    List<ExamTimeSlot> getAvailableTimeSlots(Long departmentId, LocalDate date);

    /**
     * 根据ID获取时间段
     */
    ExamTimeSlot getTimeSlot(Long id);

    /**
     * 创建时间段
     */
    void createTimeSlot(ExamTimeSlot timeSlot);

    /**
     * 更新时间段
     */
    void updateTimeSlot(ExamTimeSlot timeSlot);

    /**
     * 删除时间段
     */
    void deleteTimeSlot(Long id);

    /**
     * 更新时间段状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 增加已预约数量
     */
    void incrementBookedCount(Long id);

    /**
     * 减少已预约数量
     */
    void decrementBookedCount(Long id);
} 