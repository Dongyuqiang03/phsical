package com.shpes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shpes.entity.SysRolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 角色权限关联 Mapper 接口
 */
@Mapper
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {

    /**
     * 批量插入角色权限关联
     */
    int batchInsert(@Param("list") List<SysRolePermission> list);

    /**
     * 删除角色的所有权限关联
     */
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 获取角色的权限ID列表
     */
    List<Long> selectPermissionIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 获取用户的所有权限代码
     */
    List<String> selectUserPermissionCodes(@Param("userId") Long userId);
}