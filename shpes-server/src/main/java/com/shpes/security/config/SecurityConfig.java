package com.shpes.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shpes.common.api.CommonResult;
import com.shpes.common.enums.ResultCode;
import com.shpes.entity.SysUser;
import com.shpes.security.core.JwtAuthFilter;
import com.shpes.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring Security配置
 * 极简版本，集成所有安全相关功能
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
            // 关闭CSRF
            .csrf().disable()
            // 关闭Session
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            // 配置路径权限
            .authorizeRequests()
            // 1. 完全公开的接口
            .antMatchers(
                "/auth/login",     // 登录接口
                "/auth/captcha",   // 验证码接口
                "/auth/register"   // 注册接口
            ).permitAll()
            // 2. Swagger UI 相关接口
            .antMatchers(
                "/swagger-ui.html",
                "/swagger-resources/**",
                "/v2/api-docs",
                "/v3/api-docs/**",
                "/webjars/**",
                "/doc.html"
            ).permitAll()
            // 3. 其他所有接口都需要认证
            .anyRequest().authenticated()
            .and()
            // 配置异常处理
            .exceptionHandling()
            .authenticationEntryPoint((request, response, authException) -> {
                log.debug("认证失败，请求路径: {}", request.getRequestURI());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                objectMapper.writeValue(response.getOutputStream(), 
                    CommonResult.failed(ResultCode.UNAUTHORIZED));
            })
            .accessDeniedHandler((request, response, accessDeniedException) -> {
                log.debug("访问被拒绝，请求路径: {}", request.getRequestURI());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                objectMapper.writeValue(response.getOutputStream(), 
                    CommonResult.failed(ResultCode.FORBIDDEN));
            })
            .and()
            // 添加 JWT 过滤器
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            // 设置自定义 UserDetailsService
            .userDetailsService(userDetailsService);

        // 允许跨域
        http.cors();

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}