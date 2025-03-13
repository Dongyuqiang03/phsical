package com.shpes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shpes.entity.ExamPackage;
import com.shpes.vo.ExamItemVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 体检套餐Mapper接口
 */
@Mapper
public interface ExamPackageMapper extends BaseMapper<ExamPackage> {

    /**
     * 删除套餐的所有项目配置
     */
    int deletePackageItems(@Param("packageId") Long packageId);

    /**
     * 批量插入套餐项目配置
     */
    int insertPackageItems(@Param("packageId") Long packageId, @Param("itemIds") List<Long> itemIds);

    /**
     * 获取套餐的所有项目
     */
    List<ExamItemVO> selectPackageItems(@Param("packageId") Long packageId);

    /**
     * 获取套餐统计信息
     */
    Map<String, Object> selectPackageStats();
}