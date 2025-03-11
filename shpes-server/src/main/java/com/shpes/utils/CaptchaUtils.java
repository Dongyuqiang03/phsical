package com.shpes.utils;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.shpes.vo.CaptchaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码工具类
 */
@Component
public class CaptchaUtils {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final Producer producer;
    private static final String CAPTCHA_PREFIX = "captcha:";
    private static final long CAPTCHA_EXPIRATION = 5; // 验证码有效期（分钟）

    public CaptchaUtils() {
        // 配置验证码生成器
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", "yes");
        properties.setProperty("kaptcha.border.color", "105,179,90");
        properties.setProperty("kaptcha.textproducer.font.color", "blue");
        properties.setProperty("kaptcha.image.width", "160");
        properties.setProperty("kaptcha.image.height", "60");
        properties.setProperty("kaptcha.textproducer.font.size", "38");
        properties.setProperty("kaptcha.textproducer.char.space", "5");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        this.producer = defaultKaptcha;
    }

    /**
     * 生成验证码
     */
    public CaptchaVO generateCaptcha() {
        // 生成验证码文本
        String code = producer.createText();
        
        // 生成验证码图片
        BufferedImage image = producer.createImage(code);
        
        // 将图片转换为Base64
        String base64Image = imageToBase64(image);
        
        // 生成验证码key
        String key = UUID.randomUUID().toString();
        
        // 将验证码存入Redis
        redisTemplate.opsForValue().set(
                CAPTCHA_PREFIX + key,
                code,
                CAPTCHA_EXPIRATION,
                TimeUnit.MINUTES
        );
        
        return new CaptchaVO(key, base64Image);
    }

    /**
     * 验证验证码
     */
    public boolean verify(String key, String code) {
        if (key == null || code == null) {
            return false;
        }
        
        String cacheCode = redisTemplate.opsForValue().get(CAPTCHA_PREFIX + key);
        if (cacheCode == null) {
            return false;
        }
        
        // 验证后删除验证码
        redisTemplate.delete(CAPTCHA_PREFIX + key);
        
        return code.equalsIgnoreCase(cacheCode);
    }

    /**
     * 将图片转换为Base64
     */
    private String imageToBase64(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            throw new RuntimeException("验证码图片转换失败", e);
        }
    }
} 