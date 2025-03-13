package com.shpes.security.service;

import com.shpes.dto.LoginDTO;
import com.shpes.dto.PasswordDTO;
import com.shpes.vo.LoginVO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

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

    /**
     * 根据用户名加载用户认证信息
     */
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    
    /**
     * 刷新用户认证信息
     */
    void refreshUserAuthorities(Long userId);
}