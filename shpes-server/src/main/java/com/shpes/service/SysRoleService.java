package com.shpes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shpes.common.api.CommonPage;
import com.shpes.entity.SysRole;
import com.shpes.vo.RoleVO;

import java.util.List;

/**
 * 角色服务接口
 */
public interface SysRoleService extends IService<SysRole> {

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

    /**
     * 根据角色ID列表获取角色名称列表
     */
    List<String> getRoleNamesByIds(List<Long> roleIds);

    /**
     * 获取用户角色列表
     */
    List<SysRole> getUserRoles(Long userId);

    /**
     * 获取角色的权限ID列表
     */
    List<Long> getRolePermissionIds(Long roleId);
}