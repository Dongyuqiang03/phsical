package com.shpes.service;

import com.shpes.common.api.CommonPage;
import com.shpes.entity.ExamAppointment;
import com.shpes.vo.AppointmentVO;

import java.time.LocalDate;

/**
 * 体检预约服务接口
 */
public interface ExamAppointmentService {

    /**
     * 分页查询预约
     */
    CommonPage<AppointmentVO> getAppointmentPage(Integer pageNum, Integer pageSize, Long userId, 
            LocalDate startDate, LocalDate endDate, Integer status);

    /**
     * 获取预约详情
     */
    AppointmentVO getAppointment(Long id);

    /**
     * 创建预约
     */
    void createAppointment(ExamAppointment appointment);

    /**
     * 取消预约
     */
    void cancelAppointment(Long id, String reason);

    /**
     * 更新预约时间
     */
    void updateAppointmentTime(Long id, Long timeSlotId);

    /**
     * 完成预约
     */
    void completeAppointment(Long id);
} 