package com.shpes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shpes.common.api.CommonPage;
import com.shpes.entity.SysRole;
import com.shpes.vo.PermissionTreeVO;
import com.shpes.vo.RoleDetailVO;
import com.shpes.vo.RoleVO;

import java.util.List;

/**
 * 角色服务接口
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 分页查询角色
     *
     * @param pageNum  页码
     * @param pageSize 每页记录数
     * @param name     角色名称
     * @param status   状态：0-禁用，1-启用
     * @return 角色分页列表
     */
    CommonPage<RoleDetailVO> getRolePage(Integer pageNum, Integer pageSize, String name, Integer status);

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
     * 获取所有角色（用于下拉选择）
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
     * 获取用户角色列表
     */
    List<RoleVO> getUserRoles(Long userId);

    /**
     * 获取角色权限编码列表
     */
    List<String> getRolePermissionCodes(Long roleId);

    /**
     * 根据角色ID列表获取角色VO列表（用于下拉选择）
     */
    List<RoleVO> getRoleVOsByIds(List<Long> roleIds);

    /**
     * 获取角色详情
     */
    RoleDetailVO getRoleById(Long id);

    /**
     * 根据角色ID列表获取角色编码列表
     */
    List<String> getRoleCodes(List<Long> roleIds);

    /**
     * 获取角色权限ID列表
     */
    List<Long> getRolePermissionIds(Long roleId);

    /**
     * 获取权限树
     */
    List<PermissionTreeVO> getPermissionTree();
}