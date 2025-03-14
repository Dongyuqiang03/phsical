package com.shpes.service;

import java.util.List;

/**
 * 权限管理服务接口
 */
public interface PermissionService {
    /**
     * 获取用户权限列表
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    List<String> getUserPermissions(Long userId);
    
    /**
     * 获取用户角色列表
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    List<String> getUserRoles(Long userId);
    
    /**
     * 检查用户是否有指定权限
     *
     * @param userId 用户ID
     * @param permission 权限编码
     * @return true if has permission
     */
    boolean hasPermission(Long userId, String permission);
    
    /**
     * 检查用户是否有指定角色
     *
     * @param userId 用户ID
     * @param role 角色编码
     * @return true if has role
     */
    boolean hasRole(Long userId, String role);
    
    /**
     * 刷新用户权限缓存
     *
     * @param userId 用户ID
     */
    void refreshUserPermissions(Long userId);
    
    /**
     * 获取用户所有权限（包括角色和权限编码）
     * 用于Spring Security认证
     *
     * @param userId 用户ID
     * @return 所有权限列表
     */
    List<String> getAllUserAuthorities(Long userId);
} 