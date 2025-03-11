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

import java.util.ArrayList;
import java.util.List;

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

        // 构建登录返回信息
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUsername(userDetails.getUsername());
        
        // 设置角色和权限
        List<String> roles = new ArrayList<>();
        List<String> permissions = new ArrayList<>();
        userDetails.getAuthorities().forEach(authority -> {
            String auth = authority.getAuthority();
            if (auth.startsWith("ROLE_")) {
                roles.add(auth.substring(5));
            } else {
                permissions.add(auth);
            }
        });
        loginVO.setRoles(roles);
        loginVO.setPermissions(permissions);

        return loginVO;
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