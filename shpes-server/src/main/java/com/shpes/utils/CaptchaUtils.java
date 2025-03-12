package com.shpes.utils;

import com.google.code.kaptcha.Producer;
import com.shpes.vo.CaptchaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码工具类
 */
@Component
public class CaptchaUtils {

    @Autowired
    private Producer captchaProducer;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String CAPTCHA_PREFIX = "captcha:";
    private static final long CAPTCHA_EXPIRATION = 5; // 验证码有效期（分钟）

    /**
     * 生成验证码
     */
    public CaptchaVO generateCaptcha() {
        // 生成验证码文本
        String captchaText = captchaProducer.createText();
        
        // 生成验证码图片
        BufferedImage image = captchaProducer.createImage(captchaText);
        
        // 将图片转换为Base64
        String base64Image = imageToBase64(image);
        
        // 生成验证码key
        String key = UUID.randomUUID().toString();
        
        // 将验证码存入Redis
        redisTemplate.opsForValue().set(
            CAPTCHA_PREFIX + key,
            captchaText,
            CAPTCHA_EXPIRATION,
            TimeUnit.MINUTES
        );
        
        return CaptchaVO.of(key, "data:image/png;base64," + base64Image);
    }

    /**
     * 验证验证码
     */
    public boolean verify(String key, String code) {
        if (key == null || code == null) {
            return false;
        }
        
        String cacheKey = CAPTCHA_PREFIX + key;
        String cacheCode = redisTemplate.opsForValue().get(cacheKey);
        
        if (cacheCode != null && cacheCode.equalsIgnoreCase(code)) {
            redisTemplate.delete(cacheKey);
            return true;
        }
        
        return false;
    }

    /**
     * 将图片转换为Base64编码
     */
    private String imageToBase64(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            return Base64Utils.encodeToString(bytes);
        } catch (Exception e) {
            throw new RuntimeException("验证码图片转换失败", e);
        }
    }
} 