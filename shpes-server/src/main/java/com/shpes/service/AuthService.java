package com.shpes.service;

import com.shpes.dto.LoginDTO;
import com.shpes.dto.PasswordDTO;
import com.shpes.vo.LoginVO;

import java.util.Map;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 重置密码
     */
    void resetPassword(String username);

    /**
     * 修改密码
     */
    void changePassword(PasswordDTO passwordDTO);
}