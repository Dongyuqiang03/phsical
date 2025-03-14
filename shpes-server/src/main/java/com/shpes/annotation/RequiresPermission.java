package com.shpes.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

/**
 * 权限注解
 * 用于标记需要权限验证的方法
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@PreAuthorize("hasAuthority(#permission)")
public @interface RequiresPermission {

    /**
     * 权限标识，支持多个权限，用逗号分隔
     */
    String value();

    /**
     * 是否要求所有权限都满足
     * true: 需要满足所有权限
     * false: 满足任一权限即可
     */
    boolean logical() default true;

    /**
     * 是否要求用户已登录
     * true: 需要用户登录
     * false: 不需要用户登录
     */
    boolean requireLogin() default true;
} 