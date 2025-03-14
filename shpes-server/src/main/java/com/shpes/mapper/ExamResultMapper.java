package com.shpes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shpes.entity.ExamResult;
import com.shpes.vo.ExamResultVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 体检结果Mapper接口
 */
@Mapper
public interface ExamResultMapper extends BaseMapper<ExamResult> {
    
    List<ExamResultVO> selectResultsByRecordId(@Param("recordId") Long recordId);
    
    Page<ExamResult> selectResultPage(Page<ExamResult> page,
            @Param("recordId") Long recordId,
            @Param("itemId") Long itemId,
            @Param("resultType") Integer resultType,
            @Param("reviewStatus") Integer reviewStatus);
}