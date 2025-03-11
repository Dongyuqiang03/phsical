package com.shpes.security.service;

import com.shpes.system.entity.SysRole;
import com.shpes.system.entity.SysUser;
import com.shpes.system.service.SysRoleService;
import com.shpes.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户认证服务实现类
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysRoleService roleService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名查询用户
        SysUser user = userService.getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 1) {
            throw new UsernameNotFoundException("账号已被禁用");
        }

        // 获取用户角色列表
        List<SysRole> roles = roleService.getUserRoles(user.getId());
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // 添加角色权限
        roles.forEach(role -> {
            // 添加角色
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getCode()));
            
            // 添加角色对应的权限
            List<Long> permissionIds = roleService.getRolePermissionIds(role.getId());
            permissionIds.forEach(permissionId -> 
                authorities.add(new SimpleGrantedAuthority("PERM_" + permissionId))
            );
        });

        // 构建UserDetails对象
        return new User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
} 