package com.shpes.utils;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.shpes.vo.CaptchaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 验证码工具类
 */
@Component
public class CaptchaUtils {

    @Autowired
    private DefaultKaptcha captchaProducer;

    private static final Map<String, CaptchaData> CAPTCHA_CACHE = new ConcurrentHashMap<>();
    private static final long CAPTCHA_EXPIRATION = 5; // 验证码有效期（分钟）
    private static final ScheduledExecutorService CLEANER = Executors.newSingleThreadScheduledExecutor();

    // 初始化定时清理任务
    {
        CLEANER.scheduleAtFixedRate(this::cleanExpiredCaptcha, 1, 1, TimeUnit.MINUTES);
    }

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
        
        // 保存验证码
        CAPTCHA_CACHE.put(key, new CaptchaData(captchaText, System.currentTimeMillis()));
        
        return CaptchaVO.of(key, "data:image/png;base64," + base64Image);
    }

    /**
     * 验证验证码
     */
    public boolean verify(String key, String code) {
        if (key == null || code == null) {
            return false;
        }
        
        CaptchaData captchaData = CAPTCHA_CACHE.get(key);
        if (captchaData != null && !isExpired(captchaData.getTimestamp()) 
                && captchaData.getCode().equalsIgnoreCase(code)) {
            CAPTCHA_CACHE.remove(key);
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

    /**
     * 清理过期的验证码
     */
    private void cleanExpiredCaptcha() {
        long now = System.currentTimeMillis();
        CAPTCHA_CACHE.entrySet().removeIf(entry -> isExpired(entry.getValue().getTimestamp()));
    }

    /**
     * 检查验证码是否过期
     */
    private boolean isExpired(long timestamp) {
        return System.currentTimeMillis() - timestamp > TimeUnit.MINUTES.toMillis(CAPTCHA_EXPIRATION);
    }

    /**
     * 验证码数据类
     */
    private static class CaptchaData {
        private final String code;
        private final long timestamp;

        public CaptchaData(String code, long timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }

        public String getCode() {
            return code;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}