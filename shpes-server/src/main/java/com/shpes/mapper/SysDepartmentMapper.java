package com.shpes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shpes.entity.SysDepartment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 部门Mapper接口
 */
@Mapper
public interface SysDepartmentMapper extends BaseMapper<SysDepartment> {
    
    /**
     * 获取部门列表
     */
    List<SysDepartment> getDepartmentList();

    /**
     * 批量更新部门状态
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);
} 