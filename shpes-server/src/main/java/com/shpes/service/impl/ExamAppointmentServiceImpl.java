package com.shpes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shpes.common.api.CommonPage;
import com.shpes.common.enums.ResultCode;
import com.shpes.common.exception.ApiException;
import com.shpes.entity.ExamAppointment;
import com.shpes.entity.ExamTimeSlot;
import com.shpes.entity.ExamRecord;
import com.shpes.mapper.ExamAppointmentMapper;
import com.shpes.service.ExamAppointmentService;
import com.shpes.service.ExamPackageService;
import com.shpes.service.ExamTimeSlotService;
import com.shpes.service.SysDepartmentService;
import com.shpes.service.SysUserService;
import com.shpes.service.ExamRecordService;
import com.shpes.vo.AppointmentVO;
import com.shpes.vo.ExamPackageVO;
import com.shpes.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class ExamAppointmentServiceImpl extends ServiceImpl<ExamAppointmentMapper, ExamAppointment> implements ExamAppointmentService {

    @Autowired
    private ExamTimeSlotService timeSlotService;
    
    @Autowired
    private SysUserService userService;

    @Autowired
    private ExamPackageService packageService;
    
    @Autowired
    private SysDepartmentService departmentService;

    @Autowired
    private ExamRecordService examRecordService;

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
        appointment.setStatus(3); // 设置状态为已完成
        updateById(appointment);
    }

    @Override
    public void startAppointment(Long id) {
        ExamAppointment appointment = getById(id);
        if (appointment == null) {
            throw new ApiException(ResultCode.APPOINTMENT_NOT_EXIST);
        }
        // 只有待体检状态才能开始体检
        if (appointment.getStatus() != 1) {
            throw new ApiException(ResultCode.APPOINTMENT_STATUS_INVALID, "只有待体检状态的预约才能开始体检");
        }
        
        // 将预约状态更新为"进行中"
        appointment.setStatus(2); // 设置状态为进行中
        updateById(appointment);
        
        // 创建体检记录
        createExamRecord(appointment);
    }

    @Override
    public void cancelAppointment(Long id, String reason) {
        ExamAppointment appointment = getById(id);
        if (appointment == null) {
            throw new ApiException(ResultCode.APPOINTMENT_NOT_EXIST);
        }
        
        // 减少时间段的预约数
        timeSlotService.decrementBookedCount(appointment.getTimeSlotId());
        
        appointment.setStatus(4); // 设置状态为已取消
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
        // 调用 createAppointmentAndReturn 方法，但忽略返回值
        createAppointmentAndReturn(appointment);
    }

    @Override
    public AppointmentVO createAppointmentAndReturn(ExamAppointment appointment) {
        // 检查用户ID是否已设置
        if (appointment.getUserId() == null) {
            throw new ApiException(ResultCode.PARAM_ERROR, "用户ID不能为空");
        }
        
        // 检查时间段是否存在且可用
        ExamTimeSlot timeSlot = timeSlotService.getById(appointment.getTimeSlotId());
        if (timeSlot == null) {
            throw new ApiException(ResultCode.TIME_SLOT_NOT_EXIST);
        }
        
        // 从时间段获取科室信息
        Long deptId = timeSlot.getDeptId();
        String deptName = departmentService.getDepartmentNameById(deptId);
        appointment.setDeptId(deptId);
        appointment.setDeptName(deptName);
        
        // 获取套餐信息
        if (appointment.getPackageId() != null) {
            ExamPackageVO packageVO = packageService.getPackageById(appointment.getPackageId());
            if (packageVO != null) {
                appointment.setPackageName(packageVO.getName());
            }
        }
        
        // 生成预约编号: 格式为 YYYYMMDDHHmmssSSS + 随机4位数字
        String prefix = "AP";
        String dateStr = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String randomNum = String.format("%04d", new java.util.Random().nextInt(10000));
        String appointmentNo = prefix + dateStr + randomNum;
        
        appointment.setAppointmentNo(appointmentNo);
        appointment.setStatus(1); // 设置状态为待体检
        appointment.setAppointmentDate(timeSlot.getDate());
        save(appointment);
        
        // 增加时间段的预约数
        timeSlotService.incrementBookedCount(appointment.getTimeSlotId());
        
        // 转换为VO并返回
        return convertToVO(appointment);
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

    @Override
    public CommonPage<AppointmentVO> getUserAppointments(Long userId, Integer pageNum, Integer pageSize, 
            LocalDate startDate, LocalDate endDate, Integer status) {
        LambdaQueryWrapper<ExamAppointment> wrapper = new LambdaQueryWrapper<>();
        
        // 用户ID为必须条件
        wrapper.eq(ExamAppointment::getUserId, userId);
        
        // 添加日期过滤
        if (startDate != null) {
            wrapper.ge(ExamAppointment::getAppointmentDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(ExamAppointment::getAppointmentDate, endDate);
        }
        
        // 添加状态过滤
        if (status != null) {
            wrapper.eq(ExamAppointment::getStatus, status);
        }
        
        // 按创建时间倒序排序
        wrapper.orderByDesc(ExamAppointment::getCreateTime);

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
        
        // 加载时间段详细信息
        if (appointment.getTimeSlotId() != null) {
            try {
                ExamTimeSlot timeSlot = timeSlotService.getById(appointment.getTimeSlotId());
                if (timeSlot != null) {
                    // 设置时间段相关信息
                    vo.setStartTime(timeSlot.getStartTime());
                    vo.setEndTime(timeSlot.getEndTime());
                    vo.setAppointmentDate(timeSlot.getDate());
                    
                    // 设置科室信息（如果未设置）
                    if (vo.getDeptId() == null) {
                        vo.setDeptId(timeSlot.getDeptId());
                    }
                    
                    if (vo.getDeptName() == null && timeSlot.getDeptId() != null) {
                        try {
                            String deptName = departmentService.getDepartmentNameById(timeSlot.getDeptId());
                            vo.setDeptName(deptName);
                        } catch (Exception e) {
                            log.error("获取科室名称失败，科室ID: {}", timeSlot.getDeptId(), e);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("获取时间段信息失败，时间段ID: {}", appointment.getTimeSlotId(), e);
            }
        }
        
        return vo;
    }

    /**
     * 根据预约信息创建体检记录
     */
    private void createExamRecord(ExamAppointment appointment) {
        try {
            // 创建体检记录
            ExamRecord examRecord = new ExamRecord();
            examRecord.setAppointmentId(appointment.getId());
            examRecord.setUserId(appointment.getUserId());
            examRecord.setPackageId(appointment.getPackageId());
            examRecord.setPackageName(appointment.getPackageName());
            examRecord.setExamDate(LocalDateTime.now());
            examRecord.setStatus(1); // 1-进行中
            
            // 设置医生信息（当前登录用户）
            Long currentUserId = SecurityUtils.getCurrentUserId();
            String currentUserName = userService.getUserNameById(currentUserId);
            examRecord.setDoctorId(currentUserId);
            examRecord.setDoctorName(currentUserName);
            
            // 保存体检记录
            examRecordService.createRecord(examRecord);
            
            log.info("成功为预约ID：{}创建体检记录", appointment.getId());
        } catch (Exception e) {
            log.error("创建体检记录失败，预约ID：{}", appointment.getId(), e);
            // 这里我们不抛出异常，以避免影响预约状态更新
        }
    }
}