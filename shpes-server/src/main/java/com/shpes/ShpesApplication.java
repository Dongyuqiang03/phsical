package com.shpes;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 校医院体检信息系统启动类
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.shpes.mapper")
public class ShpesApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShpesApplication.class, args);
    }
}