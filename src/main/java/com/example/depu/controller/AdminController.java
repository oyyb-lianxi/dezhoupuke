package com.example.depu.controller;

import com.example.depu.model.User;
import com.example.depu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private UserService userService;
    
    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }
    
    // 重置用户密码
    @PostMapping("/reset-password")
    public Map<String, Object> resetPassword(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String userId = request.get("userId");
            String newPassword = request.get("newPassword");
            
            // 验证参数
            if (userId == null || newPassword == null) {
                response.put("success", false);
                response.put("message", "参数不完整");
                return response;
            }
            
            // 重置密码
            userService.resetUserPassword(userId, newPassword);
            
            response.put("success", true);
            response.put("message", "密码重置成功");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
}
