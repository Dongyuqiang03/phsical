package com.shpes.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shpes.entity.ExamResult;

import java.util.List;

/**
 * 体检结果服务接口
 */
public interface ExamResultService {

    /**
     * 获取体检记录的所有结果
     */
    List<ExamResult> getRecordResults(Long recordId);

    /**
     * 获取体检结果详情
     */
    ExamResult getResult(Long id);

    /**
     * 批量创建体检结果
     */
    void createResults(List<ExamResult> results);

    /**
     * 更新体检结果
     */
    void updateResult(ExamResult result);

    /**
     * 复核体检结果
     */
    void reviewResult(Long id, Long reviewerId);

    /**
     * 获取待复核的结果列表
     */
    Page<ExamResult> getPendingReviewResults(Integer pageNum, Integer pageSize, Long departmentId);

    /**
     * 获取异常结果列表
     */
    Page<ExamResult> getAbnormalResults(Integer pageNum, Integer pageSize, Long recordId);
} 