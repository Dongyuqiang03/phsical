package com.shpes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shpes.common.api.ApiException;
import com.shpes.common.api.ResultCode;
import com.shpes.entity.ExamAppointment;
import com.shpes.entity.ExamRecord;
import com.shpes.mapper.ExamRecordMapper;
import com.shpes.service.ExamAppointmentService;
import com.shpes.service.ExamRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 体检记录服务实现类
 */
@Service
public class ExamRecordServiceImpl implements ExamRecordService {

    @Autowired
    private ExamRecordMapper recordMapper;

    @Autowired
    private ExamAppointmentService appointmentService;

    @Override
    public Page<ExamRecord> getRecordPage(Integer pageNum, Integer pageSize, Long userId, LocalDate startDate, LocalDate endDate, Integer status) {
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(ExamRecord::getUserId, userId);
        }
        if (startDate != null) {
            wrapper.ge(ExamRecord::getCreateTime, startDate.atStartOfDay());
        }
        if (endDate != null) {
            wrapper.le(ExamRecord::getCreateTime, endDate.plusDays(1).atStartOfDay());
        }
        if (status != null) {
            wrapper.eq(ExamRecord::getStatus, status);
        }
        wrapper.orderByDesc(ExamRecord::getCreateTime);
        return recordMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public ExamRecord getRecord(Long id) {
        ExamRecord record = recordMapper.selectById(id);
        if (record == null) {
            throw new ApiException(ResultCode.EXAM_RECORD_NOT_EXIST);
        }
        return record;
    }

    @Override
    @Transactional
    public void createRecord(Long appointmentId) {
        // 检查预约是否存在且状态为待体检
        ExamAppointment appointment = appointmentService.getAppointment(appointmentId);
        if (appointment.getStatus() != 1) {
            throw new ApiException(ResultCode.FAILED.getMessage("只能为待体检的预约创建记录"));
        }

        // 检查是否已存在体检记录
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getAppointmentId, appointmentId);
        if (recordMapper.selectCount(wrapper) > 0) {
            throw new ApiException(ResultCode.FAILED.getMessage("该预约已存在体检记录"));
        }

        // 创建体检记录
        ExamRecord record = new ExamRecord();
        record.setAppointmentId(appointmentId);
        record.setExamNo(generateExamNo());
        record.setStatus(1); // 进行中
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        recordMapper.insert(record);

        // 更新预约状态为进行中
        appointmentService.completeAppointment(appointmentId);
    }

    @Override
    public void updateConclusion(Long id, String conclusion, String suggestion) {
        ExamRecord record = getRecord(id);
        if (record.getStatus() != 1) {
            throw new ApiException(ResultCode.FAILED.getMessage("只能更新进行中的体检记录"));
        }

        record.setConclusion(conclusion);
        record.setSuggestion(suggestion);
        record.setUpdateTime(LocalDateTime.now());
        recordMapper.updateById(record);
    }

    @Override
    @Transactional
    public void completeRecord(Long id, Long doctorId) {
        ExamRecord record = getRecord(id);
        if (record.getStatus() != 1) {
            throw new ApiException(ResultCode.FAILED.getMessage("只能完成进行中的体检记录"));
        }

        record.setStatus(2); // 已完成
        record.setDoctorId(doctorId);
        record.setCompleteTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        recordMapper.updateById(record);
    }

    @Override
    public Page<ExamRecord> getUserRecords(Long userId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getUserId, userId)
                .orderByDesc(ExamRecord::getCreateTime);
        return recordMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    /**
     * 生成体检编号
     */
    private String generateExamNo() {
        return "EX" + System.currentTimeMillis();
    }
} 