package com.shpes.annotation;

import java.lang.annotation.*;

/**
 * 权限注解
 * 用于标记需要权限验证的方法
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RequiresPermission {

    /**
     * 权限标识
     */
    String value();

    /**
     * 是否要求用户已登录
     */
    boolean requireLogin() default true;
} 