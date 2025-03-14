package com.shpes.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * 密码工具类
 */
@Component
public class PasswordUtils {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 8;

    /**
     * 密码加密
     */
    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    /**
     * 密码匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 生成随机密码
     */
    public static String generateRandomPassword() {
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return password.toString();
    }
    
    /**
     * 检查密码强度
     * @return true 如果密码符合要求
     */
    public boolean checkPasswordStrength(String password) {
        if (password == null || password.length() < 6 || password.length() > 20) {
            return false;
        }
        
        boolean hasLetter = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else {
                hasSpecial = true;
            }
        }
        
        return hasLetter && (hasDigit || hasSpecial);
    }
} 