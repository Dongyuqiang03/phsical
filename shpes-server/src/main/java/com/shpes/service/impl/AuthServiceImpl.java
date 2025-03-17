package com.shpes.service.impl;

import com.shpes.common.exception.ApiException;
import com.shpes.common.enums.ResultCode;
import com.shpes.dto.LoginDTO;
import com.shpes.dto.PasswordDTO;
import com.shpes.dto.RegisterDTO;
import com.shpes.entity.SysUser;
import com.shpes.security.core.JwtUtils;
import com.shpes.service.AuthService;
import com.shpes.service.PermissionService;
import com.shpes.service.SysUserService;
import com.shpes.utils.PasswordUtils;
import com.shpes.vo.LoginVO;
import com.shpes.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 认证服务实现类
 * 极简版本，直接使用SysUser对象
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SysUserService userService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 使用Spring Security进行认证
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 获取用户信息
        SysUser user = userService.getByUsername(loginDTO.getUsername());
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        // 获取用户权限
        List<String> permissions = permissionService.getUserPermissions(user.getId());
        List<String> roles = permissionService.getUserRoles(user.getId());
        userVO.setPermissions(permissions);
        userVO.setRoles(roles);

        // 生成token
        String token = jwtUtils.generateToken(user);

        // 返回登录结果
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUser(userVO);
        return loginVO;
    }

    @Override
    @Transactional
    public UserVO register(RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        if (userService.checkUsernameExists(registerDTO.getUsername())) {
            throw new ApiException(ResultCode.DUPLICATE_USERNAME);
        }

        // 创建用户
        SysUser user = new SysUser();
        BeanUtils.copyProperties(registerDTO, user);
        user.setPassword(PasswordUtils.encode(registerDTO.getPassword()));
        user.setStatus(1); // 启用状态

        userService.save(user);

        // 返回用户信息
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public void updatePassword(Long userId, PasswordDTO passwordDTO) {
        SysUser user = userService.getById(userId);
        if (user == null) {
            throw new ApiException(ResultCode.USER_NOT_EXIST);
        }

        // 验证旧密码
        if (!PasswordUtils.matches(passwordDTO.getOldPassword(), user.getPassword())) {
            throw new ApiException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 更新密码
        userService.updatePassword(userId, PasswordUtils.encode(passwordDTO.getNewPassword()));
    }

    @Override
    public void resetPassword(String username) {
        SysUser user = userService.getByUsername(username);
        if (user == null) {
            throw new ApiException(ResultCode.USER_NOT_EXIST);
        }

        userService.updatePassword(user.getId(), PasswordUtils.encode("123456"));

        // TODO: 发送新密码到用户邮箱
    }

    @Override
    public void logout(Long userId) {
        // 清除用户权限缓存
        permissionService.refreshUserPermissions(userId);
        SecurityContextHolder.clearContext();
    }

    @Override
    public LoginVO refreshToken(Long userId) {
        SysUser user = userService.getById(userId);
        if (user == null) {
            throw new ApiException(ResultCode.USER_NOT_EXIST);
        }

        // 获取用户信息
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        // 获取用户权限
        List<String> permissions = permissionService.getUserPermissions(userId);
        List<String> roles = permissionService.getUserRoles(userId);
        userVO.setPermissions(permissions);
        userVO.setRoles(roles);

        // 生成新token
        String token = jwtUtils.generateToken(user);

        // 返回新token
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUser(userVO);
        return loginVO;
    }

    @Override
    public UserVO getCurrentUser(Long userId) {
        SysUser user = userService.getById(userId);
        if (user == null) {
            throw new ApiException(ResultCode.USER_NOT_EXIST);
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        // 获取用户权限
        List<String> permissions = permissionService.getUserPermissions(userId);
        List<String> roles = permissionService.getUserRoles(userId);
        userVO.setPermissions(permissions);
        userVO.setRoles(roles);

        return userVO;
    }
}