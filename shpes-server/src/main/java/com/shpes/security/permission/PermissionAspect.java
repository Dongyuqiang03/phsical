package com.shpes.security.permission;

import com.shpes.annotation.RequiresPermission;
import com.shpes.common.exception.ApiException;
import com.shpes.common.enums.ResultCode;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限切面
 * 用于处理权限注解的切面类
 */
@Aspect
@Component
public class PermissionAspect {

    @Before("@annotation(requiresPermission)")
    public void checkPermission(RequiresPermission requiresPermission) {
        // 检查是否需要登录
        if (requiresPermission.requireLogin()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new ApiException(ResultCode.UNAUTHORIZED);
            }
        }

        // 获取所需权限列表
        List<String> requiredPermissions = Arrays.stream(requiresPermission.value().split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        // 如果不需要登录，直接返回
        if (!requiresPermission.requireLogin()) {
            return;
        }

        // 获取用户权限列表
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> userPermissions = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());

        // 根据逻辑判断权限
        if (requiresPermission.logical()) {
            // 需要满足所有权限
            boolean hasAllPermissions = requiredPermissions.stream()
                    .allMatch(permission -> userPermissions.contains(permission));
            if (!hasAllPermissions) {
                throw new ApiException(ResultCode.FORBIDDEN);
            }
        } else {
            // 满足任一权限即可
            boolean hasAnyPermission = requiredPermissions.stream()
                    .anyMatch(permission -> userPermissions.contains(permission));
            if (!hasAnyPermission) {
                throw new ApiException(ResultCode.FORBIDDEN);
            }
        }
    }
} 