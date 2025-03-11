package com.shpes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shpes.entity.ExamItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 体检项目Mapper接口
 */
@Mapper
public interface ExamItemMapper extends BaseMapper<ExamItem> {
}