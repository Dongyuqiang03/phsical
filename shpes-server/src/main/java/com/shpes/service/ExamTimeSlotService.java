package com.shpes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shpes.common.api.CommonPage;
import com.shpes.entity.ExamTimeSlot;
import com.shpes.vo.ExamTimeSlotVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 体检时间段服务接口
 */
public interface ExamTimeSlotService extends IService<ExamTimeSlot> {

    /**
     * 分页查询时间段
     */
    CommonPage<ExamTimeSlotVO> getTimeSlotPage(Integer pageNum, Integer pageSize, Long departmentId, LocalDate date);

    /**
     * 获取可预约时间段
     */
    List<ExamTimeSlotVO> getAvailableTimeSlots(Long departmentId, LocalDate date);

    /**
     * 根据ID获取时间段
     */
    ExamTimeSlotVO getTimeSlot(Long id);

    /**
     * 创建时间段
     */
    ExamTimeSlotVO createTimeSlot(ExamTimeSlot timeSlot);

    /**
     * 更新时间段
     */
    ExamTimeSlotVO updateTimeSlot(ExamTimeSlot timeSlot);

    /**
     * 删除时间段
     */
    void deleteTimeSlot(Long id);

    /**
     * 更新时间段状态
     */
    ExamTimeSlotVO updateStatus(Long id, Integer status);

    /**
     * 增加已预约数量
     */
    void incrementBookedCount(Long id);

    /**
     * 减少已预约数量
     */
    void decrementBookedCount(Long id);
}