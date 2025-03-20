package com.shpes.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

/**
 * 密码工具类 - 使用 Hutool 实现
 */
public class PasswordUtils {
    public static final String DEFAULT_PASSWORD = "123456";
    private static final String SALT = "shpes@2024";
    private static final Digester digester = new Digester(DigestAlgorithm.SHA256);

    /**
     * 加密密码
     * 
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    public static String encode(String rawPassword) {
        // 使用 SHA256 加盐加密
        return digester.digestHex(rawPassword + SALT);
    }

    /**
     * 验证密码
     * 
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 密码是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }

    public static void main(String[] args) {
        // 测试密码加密
        System.out.println("123456 加密后：" + encode("123456"));
    }
} 