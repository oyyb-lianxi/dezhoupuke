package com.example.depu.middleware;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AuthMiddleware implements HandlerInterceptor {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 允许 OPTIONS 请求通过
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        
        // 检查是否是需要认证的路径
        String path = request.getRequestURI();
        if (path.startsWith("/api/user/login") || path.startsWith("/api/user/register")) {
            return true; // 登录和注册接口不需要认证
        }
        
        // 检查请求头中的用户信息
        String userId = request.getHeader("X-User-Id");
        if (userId == null || userId.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "未授权访问");
            response.getWriter().write(objectMapper.writeValueAsString(error));
            return false;
        }
        
        // 这里可以添加更详细的用户验证逻辑，比如检查用户是否存在、会话是否有效等
        // 暂时简单实现，只检查userId是否存在
        
        return true;
    }
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 处理请求后的逻辑
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求完成后的清理工作
    }
}
