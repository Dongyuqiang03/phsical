package com.shpes.common.constant;

/**
 * 系统常量
 */
public class SystemConstants {

    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 默认分页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 超级管理员ID
     */
    public static final Long ADMIN_ID = 1L;

    /**
     * 默认密码
     */
    public static final String DEFAULT_PASSWORD = "123456";

    /**
     * Token前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 验证码Redis前缀
     */
    public static final String CAPTCHA_PREFIX = "captcha:";

    /**
     * 验证码有效期（分钟）
     */
    public static final int CAPTCHA_EXPIRATION = 5;

    /**
     * 用户Token前缀
     */
    public static final String USER_TOKEN_PREFIX = "user_token:";

    /**
     * Token过期时间（小时）
     */
    public static final int TOKEN_EXPIRATION = 24;
} 