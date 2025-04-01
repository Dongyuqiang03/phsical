package com.shpes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shpes.common.api.CommonPage;
import com.shpes.dto.ExamTimeSlotDTO;
import com.shpes.dto.ExamTimeSlotBatchDTO;
import com.shpes.dto.ExamTimeSlotUpdateDTO;
import com.shpes.entity.ExamTimeSlot;
import com.shpes.vo.ExamTimeSlotVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 体检时间段服务接口
 */
public interface ExamTimeSlotService extends IService<ExamTimeSlot> {

    /**
     * 获取时间段列表
     */
    CommonPage<ExamTimeSlotVO> getTimeSlotPage(Integer pageNum, Integer pageSize, Long deptId, LocalDate date);

    /**
     * 获取可预约时间段
     */
    List<ExamTimeSlotVO> getAvailableTimeSlots(Long deptId, LocalDate date);

    /**
     * 根据日期获取可预约时间段（不依赖科室）
     */
    List<ExamTimeSlotVO> getAvailableTimeSlotsByDate(LocalDate date);

    /**
     * 根据ID获取时间段
     */
    ExamTimeSlotVO getTimeSlot(Long id);

    /**
     * 创建时间段
     */
    void createTimeSlot(ExamTimeSlotDTO timeSlotDTO);

    /**
     * 批量创建时间段
     */
    void batchCreateTimeSlot(ExamTimeSlotBatchDTO batchDTO);

    /**
     * 更新时间段
     */
    void updateTimeSlot(Long id, ExamTimeSlotUpdateDTO updateDTO);

    /**
     * 删除时间段
     */
    void deleteTimeSlot(Long id);

    /**
     * 增加时间段预约数
     */
    void incrementBookedCount(Long id);

    /**
     * 减少时间段预约数
     */
    void decrementBookedCount(Long id);
    
    /**
     * 根据套餐ID和日期检查是否有可用时间段
     */
    boolean hasAvailableTimeSlots(Long packageId, LocalDate date);
}