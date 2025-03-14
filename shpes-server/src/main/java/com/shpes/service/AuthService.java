package com.shpes.service;

import com.shpes.dto.LoginDTO;
import com.shpes.dto.PasswordDTO;
import com.shpes.dto.RegisterDTO;
import com.shpes.vo.LoginVO;
import com.shpes.vo.UserVO;

/**
 * 认证服务接口
 */
public interface AuthService {
    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 登录结果（包含token和用户信息）
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     * @return 注册成功的用户信息
     */
    UserVO register(RegisterDTO registerDTO);

    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param passwordDTO 密码信息
     */
    void updatePassword(Long userId, PasswordDTO passwordDTO);

    /**
     * 重置密码
     *
     * @param username 用户名
     */
    void resetPassword(String username);

    /**
     * 登出
     *
     * @param userId 用户ID
     */
    void logout(Long userId);

    /**
     * 刷新token
     *
     * @param userId 用户ID
     * @return 新的token信息
     */
    LoginVO refreshToken(Long userId);

    /**
     * 获取当前登录用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVO getCurrentUser(Long userId);
}