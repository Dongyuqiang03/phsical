package com.shpes.service;

import com.shpes.common.api.CommonPage;
import com.shpes.entity.ExamRecord;
import com.shpes.vo.ExamRecordVO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 体检记录服务接口
 */
public interface ExamRecordService {

    /**
     * 分页查询体检记录
     */
    CommonPage<ExamRecordVO> getRecordPage(Integer pageNum, Integer pageSize, Long userId, 
            Integer status, LocalDate startDate, LocalDate endDate);

    /**
     * 获取体检记录详情
     */
    ExamRecordVO getRecordById(Long id);

    /**
     * 获取用户体检记录列表
     */
    List<ExamRecordVO> getUserRecords(Long userId);

    /**
     * 创建体检记录
     */
    ExamRecordVO createRecord(ExamRecord record);

    /**
     * 更新体检记录
     */
    ExamRecordVO updateRecord(ExamRecord record);

    /**
     * 删除体检记录
     */
    void deleteRecord(Long id);

    /**
     * 更新体检状态
     */
    ExamRecordVO updateStatus(Long id, Integer status);

    /**
     * 完成体检记录
     */
    ExamRecordVO completeRecord(Long id, String conclusion, String suggestion);

    /**
     * 获取今日体检统计
     */
    Object getTodayStats();

    /**
     * 获取体检完成率统计
     */
    List<Map<String, Object>> getCompletionStats(LocalDate startDate, LocalDate endDate);
}