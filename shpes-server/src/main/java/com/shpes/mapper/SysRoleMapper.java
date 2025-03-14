package com.shpes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shpes.entity.SysRole;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SysRoleMapper extends BaseMapper<SysRole> {
    
    /**
     * 查询用户角色列表
     */
    List<SysRole> selectUserRoles(@Param("userId") Long userId);

    /**
     * 查询角色权限ID列表
     */
    List<Long> selectRolePermissionIds(@Param("roleId") Long roleId);

    /**
     * 根据角色ID列表查询权限编码列表
     */
    List<String> selectPermissionCodesByIds(@Param("roleIds") List<Long> roleIds);
}