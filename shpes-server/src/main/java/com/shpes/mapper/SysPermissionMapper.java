package com.shpes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shpes.entity.SysPermission;
import com.shpes.vo.PermissionTreeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统权限 Mapper 接口
 */
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    /**
     * 获取权限树形结构
     */
    List<PermissionTreeVO> getPermissionTree();

    /**
     * 获取子权限列表
     */
    List<PermissionTreeVO> getChildPermissions(@Param("id") Long id);

    /**
     * 获取用户权限列表
     */
    List<SysPermission> getUserPermissions(@Param("userId") Long userId);

    /**
     * 获取角色权限列表
     */
    List<SysPermission> getRolePermissions(@Param("roleId") Long roleId);

    /**
     * 批量更新权限状态
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);
}