package com.shpes.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * 验证码配置
 */
@Configuration
public class KaptchaConfig {

    @Bean
    public Producer captchaProducer() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        
        // 图片边框
        properties.setProperty("kaptcha.border", "yes");
        properties.setProperty("kaptcha.border.color", "105,179,90");
        
        // 字体颜色
        properties.setProperty("kaptcha.textproducer.font.color", "blue");
        
        // 图片宽高
        properties.setProperty("kaptcha.image.width", "160");
        properties.setProperty("kaptcha.image.height", "60");
        
        // 字体大小
        properties.setProperty("kaptcha.textproducer.font.size", "38");
        
        // 验证码长度
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        
        // 字符间距
        properties.setProperty("kaptcha.textproducer.char.space", "5");

        // 使用默认字体
        properties.setProperty("kaptcha.textproducer.font.names", "Arial,Courier");
        
        // 图片样式
        properties.setProperty("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.ShadowGimpy");
        
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        
        return defaultKaptcha;
    }
} 