package com.shpes.security.service.impl;

import com.shpes.common.api.ResultCode;
import com.shpes.common.exception.ApiException;
import com.shpes.dto.LoginDTO;
import com.shpes.dto.PasswordDTO;
import com.shpes.entity.SysRole;
import com.shpes.entity.SysUser;
import com.shpes.security.service.AuthService;
import com.shpes.service.SysRoleService;
import com.shpes.service.SysUserService;
import com.shpes.utils.JwtUtils;
import com.shpes.vo.LoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysRoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtTokenUtil;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 验证用户名密码
        SysUser user = userService.getByUsername(loginDTO.getUsername());
        if (user == null || !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new ApiException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }

        if (user.getStatus() == 1) {
            throw new ApiException(ResultCode.USER_ACCOUNT_DISABLED);
        }

        // 生成认证信息
        UserDetails userDetails = loadUserByUsername(user.getUsername());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 生成token
        String token = jwtTokenUtil.generateToken(userDetails);

        // 返回登录信息
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUsername(user.getUsername());
        return loginVO;
    }

    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(String username) {
        SysUser user = userService.getByUsername(username);
        if (user == null) {
            throw new ApiException(ResultCode.USER_NOT_EXIST);
        }
        
        // 重置为默认密码
        user.setPassword(passwordEncoder.encode("123456"));
        userService.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(PasswordDTO passwordDTO) {
        // 获取当前用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        SysUser user = userService.getByUsername(username);
        if (user == null) {
            throw new ApiException(ResultCode.USER_NOT_EXIST);
        }

        // 验证旧密码
        if (!passwordEncoder.matches(passwordDTO.getOldPassword(), user.getPassword())) {
            throw new ApiException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        userService.updateById(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userService.getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }

        if (user.getStatus() == 1) {
            throw new UsernameNotFoundException("账号已被禁用");
        }

        return new User(
                user.getUsername(),
                user.getPassword(),
                getUserAuthorities(user.getId())
        );
    }

    @Override
    public void refreshUserAuthorities(Long userId) {
        SysUser user = userService.getById(userId);
        if (user == null) {
            throw new ApiException(ResultCode.USER_NOT_EXIST);
        }

        // 刷新权限（如果用户当前已登录）
        String username = user.getUsername();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && username.equals(authentication.getName())) {
            UserDetails userDetails = loadUserByUsername(username);
            UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
    }

    private List<SimpleGrantedAuthority> getUserAuthorities(Long userId) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        List<SysRole> roles = roleService.getUserRoles(userId);
        
        roles.forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getCode()));
            List<Long> permissionIds = roleService.getRolePermissionIds(role.getId());
            permissionIds.forEach(permissionId -> 
                authorities.add(new SimpleGrantedAuthority("PERM_" + permissionId))
            );
        });
        
        return authorities;
    }
}