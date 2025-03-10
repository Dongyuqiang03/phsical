package com.shpes;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.shpes.mapper")
public class ShpesApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShpesApplication.class, args);
    }

}