package com.shpes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shpes.common.api.CommonPage;
import com.shpes.common.enums.ResultCode;
import com.shpes.common.exception.ApiException;
import com.shpes.entity.ExamAppointment;
import com.shpes.entity.ExamTimeSlot;
import com.shpes.mapper.ExamAppointmentMapper;
import com.shpes.service.ExamAppointmentService;
import com.shpes.service.ExamTimeSlotService;
import com.shpes.service.SysUserService;
import com.shpes.vo.AppointmentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class ExamAppointmentServiceImpl extends ServiceImpl<ExamAppointmentMapper, ExamAppointment> implements ExamAppointmentService {

    @Autowired
    private ExamTimeSlotService timeSlotService;
    
    @Autowired
    private SysUserService userService;

    @Override
    public AppointmentVO getAppointment(Long id) {
        ExamAppointment appointment = getById(id);
        if (appointment == null) {
            throw new ApiException(ResultCode.APPOINTMENT_NOT_EXIST);
        }
        return convertToVO(appointment);
    }

    @Override
    public void completeAppointment(Long id) {
        ExamAppointment appointment = getById(id);
        if (appointment == null) {
            throw new ApiException(ResultCode.APPOINTMENT_NOT_EXIST);
        }
        appointment.setStatus(2); // 设置状态为进行中
        updateById(appointment);
    }

    @Override
    public void cancelAppointment(Long id, String reason) {
        ExamAppointment appointment = getById(id);
        if (appointment == null) {
            throw new ApiException(ResultCode.APPOINTMENT_NOT_EXIST);
        }
        
        // 减少时间段的预约数
        timeSlotService.decrementBookedCount(appointment.getTimeSlotId());
        
        appointment.setStatus(3); // 设置状态为已取消
        appointment.setCancelReason(reason);
        updateById(appointment);
    }

    @Override
    public CommonPage<AppointmentVO> getAppointmentPage(Integer pageNum, Integer pageSize, Long userId, 
            LocalDate startDate, LocalDate endDate, Integer status) {
        LambdaQueryWrapper<ExamAppointment> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(ExamAppointment::getUserId, userId);
        }
        if (startDate != null) {
            wrapper.ge(ExamAppointment::getAppointmentDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(ExamAppointment::getAppointmentDate, endDate);
        }
        if (status != null) {
            wrapper.eq(ExamAppointment::getStatus, status);
        }
        wrapper.orderByDesc(ExamAppointment::getCreateTime);

        Page<ExamAppointment> page = page(new Page<>(pageNum, pageSize), wrapper);
        List<AppointmentVO> records = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return CommonPage.restPage(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public void createAppointment(ExamAppointment appointment) {
        // 检查时间段是否存在且可用
        ExamTimeSlot timeSlot = timeSlotService.getById(appointment.getTimeSlotId());
        if (timeSlot == null) {
            throw new ApiException(ResultCode.TIME_SLOT_NOT_EXIST);
        }
        
        appointment.setStatus(1); // 设置状态为待体检
        appointment.setAppointmentDate(timeSlot.getDate());
        save(appointment);
        
        // 增加时间段的预约数
        timeSlotService.incrementBookedCount(appointment.getTimeSlotId());
    }

    @Override
    public void updateAppointmentTime(Long id, Long timeSlotId) {
        ExamAppointment appointment = getById(id);
        if (appointment == null) {
            throw new ApiException(ResultCode.APPOINTMENT_NOT_EXIST);
        }

        // 检查新时间段是否存在且可用
        ExamTimeSlot newTimeSlot = timeSlotService.getById(timeSlotId);
        if (newTimeSlot == null) {
            throw new ApiException(ResultCode.TIME_SLOT_NOT_EXIST);
        }

        // 减少原时间段的预约数
        timeSlotService.decrementBookedCount(appointment.getTimeSlotId());
        
        // 更新预约信息
        appointment.setTimeSlotId(timeSlotId);
        appointment.setAppointmentDate(newTimeSlot.getDate());
        updateById(appointment);
        
        // 增加新时间段的预约数
        timeSlotService.incrementBookedCount(timeSlotId);
    }

    @Override
    public CommonPage<AppointmentVO> getUserAppointments(Long userId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<ExamAppointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamAppointment::getUserId, userId)
                .orderByDesc(ExamAppointment::getCreateTime);

        Page<ExamAppointment> page = page(new Page<>(pageNum, pageSize), wrapper);
        List<AppointmentVO> records = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return CommonPage.restPage(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    private AppointmentVO convertToVO(ExamAppointment appointment) {
        if (appointment == null) {
            return null;
        }
        AppointmentVO vo = new AppointmentVO();
        BeanUtils.copyProperties(appointment, vo);
        
        // 设置用户信息
        if (appointment.getUserId() != null) {
            vo.setUserName(userService.getUserNameById(appointment.getUserId()));
        }
        
        return vo;
    }
}