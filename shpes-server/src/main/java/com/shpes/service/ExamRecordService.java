package com.shpes.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shpes.entity.ExamRecord;

import java.time.LocalDate;

/**
 * 体检记录服务接口
 */
public interface ExamRecordService {

    /**
     * 分页获取体检记录列表
     */
    Page<ExamRecord> getRecordPage(Integer pageNum, Integer pageSize, Long userId, LocalDate startDate, LocalDate endDate, Integer status);

    /**
     * 获取体检记录详情
     */
    ExamRecord getRecord(Long id);

    /**
     * 创建体检记录
     */
    void createRecord(Long appointmentId);

    /**
     * 更新体检结论和建议
     */
    void updateConclusion(Long id, String conclusion, String suggestion);

    /**
     * 完成体检记录
     */
    void completeRecord(Long id, Long doctorId);

    /**
     * 获取用户的体检记录列表
     */
    Page<ExamRecord> getUserRecords(Long userId, Integer pageNum, Integer pageSize);
} 