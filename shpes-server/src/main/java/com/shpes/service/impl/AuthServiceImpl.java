package com.shpes.service.impl;

import com.shpes.dto.LoginDTO;
import com.shpes.dto.PasswordDTO;
import com.shpes.entity.SysUser;
import com.shpes.service.AuthService;
import com.shpes.utils.JwtUtils;
import com.shpes.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 进行身份验证
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
        );

        // 设置认证信息到上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 生成JWT token
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtils.generateToken(userDetails);

        // 获取用户角色
        String role = userDetails.getAuthorities().stream()
                .filter(auth -> auth.getAuthority().startsWith("ROLE_"))
                .map(auth -> auth.getAuthority().substring(5))
                .findFirst()
                .orElse("");

        // 获取部门名称（假设UserDetails是我们自定义的实现，包含了部门信息）
        String departmentName = "";
        if (userDetails instanceof SysUser) {
            departmentName = ((SysUser) userDetails).getDepartmentName();
        }

        // 返回登录信息
        return LoginVO.of(token, userDetails.getUsername(), role, departmentName);
    }

    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }

    @Override
    public void resetPassword(String username) {
        // TODO: 实现密码重置逻辑
    }

    @Override
    public void changePassword(PasswordDTO passwordDTO) {
        // TODO: 实现密码修改逻辑
    }
}