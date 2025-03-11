package com.shpes.service;

import com.shpes.common.api.CommonPage;
import com.shpes.entity.SysRole;
import com.shpes.vo.RoleVO;
import com.shpes.vo.RolePageVO;

import java.util.List;

/**
 * 角色服务接口
 */
public interface SysRoleService {

    /**
     * 分页查询角色
     */
    CommonPage<RoleVO> getRolePage(Integer pageNum, Integer pageSize, String keyword);

    /**
     * 创建角色
     */
    void createRole(SysRole role);

    /**
     * 更新角色
     */
    void updateRole(SysRole role);

    /**
     * 删除角色
     */
    void deleteRole(Long id);

    /**
     * 获取所有角色
     */
    List<RoleVO> getAllRoles();

    /**
     * 分配角色权限
     */
    void assignPermissions(Long roleId, List<Long> permissionIds);

    /**
     * 获取角色权限
     */
    List<Long> getRolePermissions(Long roleId);
} 