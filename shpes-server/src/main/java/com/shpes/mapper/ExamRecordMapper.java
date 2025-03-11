package com.shpes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shpes.entity.ExamRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 体检记录Mapper接口
 */
@Mapper
public interface ExamRecordMapper extends BaseMapper<ExamRecord> {
} 