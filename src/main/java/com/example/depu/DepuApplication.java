package com.example.depu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DepuApplication {
    private static final Logger logger = LoggerFactory.getLogger(DepuApplication.class);
    
    public static void main(String[] args) {
        SpringApplication.run(DepuApplication.class, args);
        logger.info("===== 网咖德州扑克后台启动成功 =====");
    }
}
