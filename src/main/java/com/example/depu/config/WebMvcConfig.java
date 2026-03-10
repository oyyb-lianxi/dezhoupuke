package com.example.depu.config;

import com.example.depu.middleware.AuthMiddleware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private AuthMiddleware authMiddleware;
    
    @Autowired
    public WebMvcConfig(AuthMiddleware authMiddleware) {
        this.authMiddleware = authMiddleware;
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authMiddleware)
                .addPathPatterns("/api/**") // 对所有API请求进行拦截
                .excludePathPatterns("/api/user/login", "/api/user/register"); // 排除登录和注册接口
    }
}
