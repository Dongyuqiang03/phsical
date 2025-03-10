package com.shpes.service;

import com.shpes.entity.SysUser;

public interface AuthService {
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return JWT token
     */
    String login(String username, String password);

    /**
     * 获取当前登录用户信息
     * @return 用户信息
     */
    SysUser getCurrentUser();

    /**
     * 退出登录
     */
    void logout();
}