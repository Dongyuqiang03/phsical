package com.shpes.service;

import com.shpes.common.api.CommonPage;
import com.shpes.entity.ExamRecord;
import com.shpes.vo.ExamRecordDetailVO;
import com.shpes.vo.ExamRecordPageVO;
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
    CommonPage<ExamRecordPageVO> getRecordPage(Integer pageNum, Integer pageSize, Long userId,
                                               Integer status, LocalDate startDate, LocalDate endDate);

    /**
     * 获取体检记录详情
     */
    ExamRecordDetailVO getRecordDetail(Long id);
    
    /**
     * 获取用户的体检记录列表（不分页）
     *
     * @param userId 用户ID
     * @return 体检记录列表
     */
    List<ExamRecordVO> getUserRecords(Long userId);

    /**
     * 获取用户的体检记录（分页）
     *
     * @param userId      用户ID
     * @param pageNum     页码
     * @param pageSize    页大小
     * @param beginDate   开始日期
     * @param endDate     结束日期
     * @param packageName 套餐名称
     * @param status      状态
     * @return 分页结果
     */
    CommonPage<ExamRecordPageVO> getUserRecords(Long userId, Integer pageNum, Integer pageSize,
                                         String beginDate, String endDate, String packageName, String status);

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