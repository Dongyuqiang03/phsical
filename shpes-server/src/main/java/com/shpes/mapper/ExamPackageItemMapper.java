package com.shpes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shpes.entity.ExamPackageItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 体检套餐项目关联Mapper接口
 */
@Mapper
public interface ExamPackageItemMapper extends BaseMapper<ExamPackageItem> {
} 