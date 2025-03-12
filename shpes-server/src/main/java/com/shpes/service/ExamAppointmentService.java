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
     * 获取预约详情
     */
    AppointmentVO getAppointment(Long id);

    /**
     * 完成预约（更新状态为进行中）
     */
    AppointmentVO completeAppointment(Long id);

    /**
     * 取消预约
     */
    AppointmentVO cancelAppointment(Long id, String reason);

    /**
     * 分页获取预约列表
     */
    CommonPage<AppointmentVO> getAppointmentPage(Integer pageNum, Integer pageSize, Long userId, 
            LocalDate startDate, LocalDate endDate, Integer status);

    /**
     * 创建预约
     */
    AppointmentVO createAppointment(ExamAppointment appointment);

    /**
     * 更新预约时间
     */
    AppointmentVO updateAppointmentTime(Long id, Long timeSlotId);

    /**
     * 获取用户的预约列表
     */
    CommonPage<AppointmentVO> getUserAppointments(Long userId, Integer pageNum, Integer pageSize);
}