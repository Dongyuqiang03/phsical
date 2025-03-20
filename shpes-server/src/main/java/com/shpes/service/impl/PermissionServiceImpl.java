package com.shpes.service.impl;

import com.shpes.mapper.SysRolePermissionMapper;
import com.shpes.service.PermissionService;
import com.shpes.service.SysRoleService;
import com.shpes.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限管理服务实现类
 */
@Slf4j
@Service
@CacheConfig(cacheNames = "permission")
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysRoleService roleService;
    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Override
    @Cacheable(key = "'perm:' + #userId")
    public List<String> getUserPermissions(Long userId) {
        return sysRolePermissionMapper.selectUserPermissionCodes(userId);
    }

    @Override
    @Cacheable(key = "'role:' + #userId")
    public List<String> getUserRoles(Long userId) {
        return userService.getUserRoleCodes(userId);
    }

    @Override
    public boolean hasPermission(Long userId, String permission) {
        if (permission == null || permission.isEmpty()) {
            return true;
        }
        List<String> permissions = getUserPermissions(userId);
        return permissions.contains(permission);
    }

    @Override
    public boolean hasRole(Long userId, String role) {
        if (role == null || role.isEmpty()) {
            return true;
        }
        List<String> roles = getUserRoles(userId);
        return roles.contains(role);
    }

    @Override
    @CacheEvict(allEntries = true)
    public void refreshUserPermissions(Long userId) {
        log.info("Refreshing permissions cache for user: {}", userId);
    }

    @Override
    @Cacheable(key = "'auth:' + #userId")
    public List<String> getAllUserAuthorities(Long userId) {
        Set<String> authorities = new HashSet<>();
        // 添加角色
        List<String> roles = getUserRoles(userId);
        authorities.addAll(roles.stream()
                .map(role -> "ROLE_" + role)
                .collect(Collectors.toList()));
        // 添加权限
        authorities.addAll(getUserPermissions(userId));
        return new ArrayList<>(authorities);
    }
} 