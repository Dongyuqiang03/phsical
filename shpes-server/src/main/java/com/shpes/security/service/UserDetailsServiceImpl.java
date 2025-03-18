package com.shpes.security.service;

import com.shpes.entity.SysUser;
import com.shpes.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userService.getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new UsernameNotFoundException("账号已被禁用");
        }
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            "",  // 空密码，因为我们使用自己的密码验证
            userService.getUserAuthorities(user.getId())
        );
    }
} 