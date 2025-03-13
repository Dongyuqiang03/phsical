package com.shpes.service;

import com.shpes.common.api.CommonPage;
import com.shpes.entity.ExamResult;
import com.shpes.vo.ExamResultVO;

import java.util.List;

public interface ExamResultService {
    /**
     * 获取体检记录的结果列表
     */
    List<ExamResultVO> getResultsByRecordId(Long recordId);

    /**
     * 分页查询体检结果
     */
    CommonPage<ExamResultVO> getResultPage(Integer pageNum, Integer pageSize, Long recordId, 
            Long itemId, Integer resultType, Integer reviewStatus);

    /**
     * 创建体检结果
     */
    ExamResultVO createResult(ExamResult result);

    /**
     * 批量创建体检结果
     */
    List<ExamResultVO> createResults(List<ExamResult> results);

    /**
     * 更新体检结果
     */
    ExamResultVO updateResult(ExamResult result);

    /**
     * 删除体检结果
     */
    void deleteResult(Long id);

    /**
     * 复核体检结果
     */
    ExamResultVO reviewResult(Long id, String suggestion);

    /**
     * 批量复核体检结果
     */
    List<ExamResultVO> reviewResults(List<Long> ids);

    /**
     * 导出体检报告
     */
    String exportReport(Long recordId);
}