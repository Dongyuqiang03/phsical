package com.shpes.security.permission;

import com.shpes.annotation.RequiresPermission;
import com.shpes.common.exception.ApiException;
import com.shpes.common.enums.ResultCode;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 权限切面
 * 用于处理权限注解的切面类
 */
@Aspect
@Component
public class PermissionAspect {

    @Before("@annotation(requiresPermission)")
    public void checkPermission(RequiresPermission requiresPermission) {
        // 获取当前认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 检查是否需要登录
        if (requiresPermission.requireLogin()) {
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new ApiException(ResultCode.UNAUTHORIZED);
            }
        }

        // 获取注解中的权限值
        String requiredPermission = requiresPermission.value();

        // 获取用户权限列表
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean hasPermission = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(requiredPermission));

        // 如果没有所需权限，抛出异常
        if (!hasPermission) {
            throw new ApiException(ResultCode.FORBIDDEN);
        }
    }
} 