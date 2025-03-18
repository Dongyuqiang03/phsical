package com.shpes.security.core;

import com.shpes.entity.SysUser;
import com.shpes.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * JWT认证过滤器
 * 只负责解析和验证 JWT token，不处理认证错误
 * 认证错误由 SecurityConfig 中配置的 AuthenticationEntryPoint 统一处理
 */
@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    
    private final SysUserService userService;
    private final JwtUtils jwtUtils;

    public JwtAuthFilter(SysUserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 从请求头获取JWT
        String authHeader = request.getHeader("Authorization");
        
        // 如果没有token，直接放行，让 SecurityConfig 处理
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = authHeader.substring(7);
            
            // 验证token
            if (jwtUtils.validateToken(jwt)) {
                // 从JWT中获取用户ID
                Long userId = jwtUtils.getUserIdFromToken(jwt);
                if (userId != null) {
                    // 获取用户信息
                    SysUser user = userService.getById(userId);
                    if (user != null && user.getStatus() == 1) {
                        // 获取用户权限
                        List<SimpleGrantedAuthority> authorities = userService.getUserAuthorities(userId);
                        
                        // 创建认证对象
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                user, null, authorities);
                        
                        // 设置认证信息
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug("Authentication successful for user: {}", user.getUsername());
                    }
                }
            }
        } catch (Exception e) {
            log.error("JWT authentication failed: {}", e.getMessage());
            // 清除认证信息，让 SecurityConfig 的 AuthenticationEntryPoint 处理
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}