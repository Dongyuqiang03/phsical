package com.shpes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shpes.entity.ExamRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 体检记录Mapper接口
 */
@Mapper
public interface ExamRecordMapper extends BaseMapper<ExamRecord> {

    /**
     * 获取今日体检统计
     */
    Map<String, Object> selectTodayStats();

    /**
     * 获取体检完成率统计
     */
    List<Map<String, Object>> selectCompletionStats(@Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);
}