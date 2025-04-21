package com.shpes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shpes.entity.ExamRecord;
import com.shpes.vo.ExamRecordDetailVO;
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

    /**
     * 获取体检记录详情
     * @param id 记录ID
     * @return 体检记录详情VO
     */
    ExamRecordDetailVO getRecordDetail(@Param("id") Long id);
}