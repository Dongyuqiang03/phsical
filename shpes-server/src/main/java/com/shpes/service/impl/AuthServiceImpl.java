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
import com.shpes.service.SysRoleService;
import com.shpes.service.SysUserService;
import com.shpes.utils.PasswordUtils;
import com.shpes.vo.LoginVO;
import com.shpes.vo.RoleVO;
import com.shpes.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 认证服务实现类
 * 极简版本，直接使用SysUser对象
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserService userService;
    private final PermissionService permissionService;
    private final SysRoleService roleService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        try {
            log.info("开始登录，用户名：{}", loginDTO.getUsername());
            
            // 获取用户信息
            SysUser user = userService.getByUsername(loginDTO.getUsername());
            if (user == null) {
                log.error("用户不存在：{}", loginDTO.getUsername());
                throw new UsernameNotFoundException("用户不存在");
            }
            
            // 验证密码
            if (!PasswordUtils.matches(loginDTO.getPassword(), user.getPassword())) {
                log.error("密码错误：{}", loginDTO.getUsername());
                throw new BadCredentialsException("密码错误");
            }

            // 检查用户状态
            if (user.getStatus() == 0) {
                throw new ApiException(ResultCode.USER_DISABLED);
            }

            // 创建认证信息
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                null,
                userService.getUserAuthorities(user.getId())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);

            // 获取用户权限和角色
            List<String> permissions = permissionService.getUserPermissions(user.getId());
            List<RoleVO> roles = roleService.getUserRoles(user.getId());
            userVO.setPermissions(permissions);
            userVO.setRoles(roles);
            log.debug("获取用户权限：permissions={}, roles={}", permissions, roles);

            // 生成token
            String token = jwtUtils.generateToken(user);
            log.debug("生成token：{}", token);

            // 返回登录结果
            LoginVO loginVO = new LoginVO();
            loginVO.setToken(token);
            loginVO.setUser(userVO);
            return loginVO;
        } catch (BadCredentialsException e) {
            log.error("登录失败，用户名或密码错误：{}", e.getMessage(), e);
            throw new ApiException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        } catch (UsernameNotFoundException e) {
            log.error("登录失败，用户不存在：{}", e.getMessage(), e);
            throw new ApiException(ResultCode.USER_NOT_EXIST);
        } catch (Exception e) {
            log.error("登录失败，系统异常：", e);
            throw new ApiException(ResultCode.FAILED);
        }
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

        // 获取用户权限和角色
        List<String> permissions = permissionService.getUserPermissions(userId);
        List<RoleVO> roles = roleService.getUserRoles(userId);
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

        // 获取用户权限和角色
        List<String> permissions = permissionService.getUserPermissions(userId);
        List<RoleVO> roles = roleService.getUserRoles(userId);
        userVO.setPermissions(permissions);
        userVO.setRoles(roles);

        return userVO;
    }
}