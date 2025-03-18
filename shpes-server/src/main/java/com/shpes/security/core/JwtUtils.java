package com.shpes.security.core;

import com.shpes.entity.SysUser;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 简化版本，提供JWT的生成、解析和验证功能
 */
@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private int expiration;

    @Value("${jwt.prefix:Bearer }")
    private String tokenPrefix;

    /**
     * 从用户信息生成JWT令牌
     */
    public String generateToken(SysUser user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        claims.put("realName", user.getRealName());
        claims.put("deptId", user.getDeptId());
        claims.put("userType", user.getUserType());
        
        return generateToken(claims);
    }

    /**
     * 生成JWT令牌
     */
    private String generateToken(Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 从JWT令牌中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return Long.valueOf(claims.get("id").toString());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从JWT令牌中获取用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return (String) claims.get("username");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从JWT令牌中获取Claims
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证JWT令牌
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            log.error("无效的JWT签名: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("无效的JWT令牌: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT令牌已过期: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("不支持的JWT令牌: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT声明为空: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 从请求头中获取JWT令牌
     */
    public String getTokenFromHeader(String header) {
        if (header != null && header.startsWith(tokenPrefix)) {
            return header.substring(tokenPrefix.length());
        }
        return null;
    }
} 