package com.shpes.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus 配置类
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 配置 MyBatis Plus 插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        // 添加分页插件
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInterceptor.setOverflow(true); // 设置请求的页面大于最大页后是否返回首页，默认false
        paginationInterceptor.setMaxLimit(500L); // 设置最大单页限制数量，默认 500 条，-1 不受限制
        
        interceptor.addInnerInterceptor(paginationInterceptor);
        
        return interceptor;
    }
} 