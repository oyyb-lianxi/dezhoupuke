package com.example.depu.controller;

import com.example.depu.model.User;
import com.example.depu.model.Player;
import com.example.depu.service.UserService;
import com.example.depu.manager.PlayerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private UserService userService;
    private PlayerManager playerManager;
    
    @Autowired
    public UserController(UserService userService, PlayerManager playerManager) {
        this.userService = userService;
        this.playerManager = playerManager;
    }
    
    // 用户注册
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String idCard = request.get("idCard");
            String password = request.get("password");
            String name = request.get("name");
            String nickname = request.get("nickname");
            int age = Integer.parseInt(request.get("age"));
            
            // 注册用户
            User user = userService.register(idCard, password, name, age);
            
            // 创建玩家
            Player player = userService.createPlayer(user, nickname);
            playerManager.addPlayer(player);
            
            response.put("success", true);
            response.put("message", "注册成功");
            response.put("userId", user.getId());
            response.put("playerId", player.getId());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
    
    // 用户登录
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String idCard = request.get("idCard");
            String password = request.get("password");
            
            // 登录验证
            User user = userService.login(idCard, password);
            
            // 查找或创建玩家
            Player player = playerManager.getPlayerByUserId(user.getId());
            if (player == null) {
                player = userService.createPlayer(user, user.getName());
                playerManager.addPlayer(player);
            }
            
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("userId", user.getId());
            response.put("playerId", player.getId());
            response.put("nickname", player.getNickname());
            response.put("warehousePoints", player.getWarehousePoints());
            response.put("level", player.getLevel());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
    
    // 获取用户信息
    @GetMapping("/info")
    public Map<String, Object> getUserInfo(@RequestParam String userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                response.put("success", false);
                response.put("message", "用户不存在");
                return response;
            }
            
            Player player = playerManager.getPlayerByUserId(userId);
            
            response.put("success", true);
            response.put("user", user);
            response.put("player", player);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
    
    // 修改密码
    @PostMapping("/change-password")
    public Map<String, Object> changePassword(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String userId = request.get("userId");
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");
            
            // 验证参数
            if (userId == null || oldPassword == null || newPassword == null) {
                response.put("success", false);
                response.put("message", "参数不完整");
                return response;
            }
            
            // 修改密码
            userService.changePassword(userId, oldPassword, newPassword);
            
            response.put("success", true);
            response.put("message", "密码修改成功");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
}
