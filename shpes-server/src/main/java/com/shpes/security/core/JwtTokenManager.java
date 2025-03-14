package com.shpes.security.core;

import com.shpes.common.exception.ApiException;
import com.shpes.common.enums.ResultCode;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT令牌管理类
 */
@Slf4j
@Component
public class JwtTokenManager {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpiration;

    @Value("${jwt.header:Authorization}")
    private String jwtHeader;

    @Value("${jwt.prefix:Bearer }")
    private String jwtPrefix;

    /**
     * 从认证信息生成JWT令牌
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        return generateTokenFromUsername(username);
    }

    /**
     * 从用户详情生成JWT令牌
     */
    public String generateToken(UserDetails userDetails) {
        return generateTokenFromUsername(userDetails.getUsername());
    }

    /**
     * 从用户名生成JWT令牌
     */
    private String generateTokenFromUsername(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration * 1000);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * 从JWT令牌中获取用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (Exception e) {
            log.error("Error getting username from token: {}", e.getMessage());
            throw new ApiException(ResultCode.UNAUTHORIZED);
        }
    }

    /**
     * 验证JWT令牌
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 从请求头中获取JWT令牌
     */
    public String getTokenFromHeader(String header) {
        if (header != null && header.startsWith(jwtPrefix)) {
            return header.substring(jwtPrefix.length());
        }
        return null;
    }

    /**
     * 获取JWT令牌过期时间
     */
    public Date getExpirationFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration();
        } catch (Exception e) {
            log.error("Error getting expiration from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 检查JWT令牌是否过期
     */
    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationFromToken(token);
        return expiration != null && expiration.before(new Date());
    }
} 