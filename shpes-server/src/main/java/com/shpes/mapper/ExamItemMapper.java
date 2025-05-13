package com.shpes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shpes.entity.ExamItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 体检项目Mapper接口
 */
@Mapper
public interface ExamItemMapper extends BaseMapper<ExamItem> {

    @Select("SELECT COALESCE(SUM(price), 0) FROM exam_item WHERE id IN (#{itemIds})")
    Integer selectItemsPriceSum(@Param("itemIds") List<Long> itemIds);
}