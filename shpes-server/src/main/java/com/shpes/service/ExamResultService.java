package com.shpes.service;

import com.shpes.common.api.CommonPage;
import com.shpes.entity.ExamResult;
import com.shpes.vo.ExamResultVO;
import com.shpes.dto.ExamResultBatchDTO;

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

    /**
     * 根据批量DTO保存体检结果
     * @param batchDTO 批量DTO
     * @return 保存后的体检结果列表
     */
    List<ExamResultVO> createResultsFromBatchDTO(ExamResultBatchDTO batchDTO);

    /**
     * 获取用户的体检结果（分页）
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @param recordId 体检记录ID
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @param itemName 项目名称
     * @param resultType 结果类型（0-正常，1-异常）
     * @return 分页结果
     */
    CommonPage<ExamResultVO> getCurrentUserResults(Long userId, Integer pageNum, Integer pageSize,
                                                 Long recordId, String beginDate, String endDate,
                                                 String itemName, Integer resultType);
}