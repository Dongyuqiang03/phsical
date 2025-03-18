package com.shpes.utils;

import com.shpes.common.exception.ApiException;
import com.shpes.common.enums.ResultCode;
import com.shpes.entity.SysUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类
 * 极简版本，直接从SecurityContextHolder获取SysUser对象
 */
public class SecurityUtils {

    /**
     * 获取当前登录用户
     * 
     * @return 当前登录用户
     * @throws ApiException 如果用户未登录
     */
    public static SysUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApiException(ResultCode.UNAUTHORIZED);
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof SysUser) {
            return (SysUser) principal;
        }
        
        throw new ApiException(ResultCode.UNAUTHORIZED);
    }
    
    /**
     * 获取当前登录用户ID
     * 
     * @return 当前登录用户ID
     * @throws ApiException 如果用户未登录
     */
    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
    
    /**
     * 获取当前登录用户的用户名
     * 
     * @return 当前登录用户的用户名
     * @throws ApiException 如果用户未登录
     */
    public static String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }
    
    /**
     * 判断当前用户是否已登录
     * 
     * @return 如果已登录返回true，否则返回false
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
} 