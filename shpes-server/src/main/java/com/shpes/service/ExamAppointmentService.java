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
     * 完成预约（更新状态为已完成）
     */
    void completeAppointment(Long id);

    /**
     * 开始体检（更新状态为进行中）
     */
    void startAppointment(Long id);

    /**
     * 取消预约
     */
    void cancelAppointment(Long id, String reason);

    /**
     * 分页获取预约列表
     */
    CommonPage<AppointmentVO> getAppointmentPage(Integer pageNum, Integer pageSize, Long userId, 
            LocalDate startDate, LocalDate endDate, Integer status);

    /**
     * 创建预约
     */
    void createAppointment(ExamAppointment appointment);

    /**
     * 创建预约并返回详情
     */
    AppointmentVO createAppointmentAndReturn(ExamAppointment appointment);

    /**
     * 更新预约时间
     */
    void updateAppointmentTime(Long id, Long timeSlotId);

    /**
     * 获取用户的预约列表
     */
    CommonPage<AppointmentVO> getUserAppointments(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取用户的预约列表（带过滤条件）
     */
    CommonPage<AppointmentVO> getUserAppointments(Long userId, Integer pageNum, Integer pageSize, 
            LocalDate startDate, LocalDate endDate, Integer status);
}