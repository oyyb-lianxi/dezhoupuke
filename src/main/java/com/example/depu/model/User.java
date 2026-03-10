package com.example.depu.model;

import java.time.LocalDateTime;

public class User implements java.io.Serializable {
    /**
     * 用户唯一标识符
     */
    private String id;
    
    /**
     * 身份证号（登录账号）
     */
    private String idCard;
    
    /**
     * 密码（加密存储）
     */
    private String password;
    
    /**
     * 用户名
     */
    private String name;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    
    /**
     * 状态（ACTIVE, INACTIVE, BANNED）
     */
    private UserStatus status;
    
    public enum UserStatus {
        ACTIVE, INACTIVE, BANNED
    }
    
    public User() {
        this.createTime = LocalDateTime.now();
        this.status = UserStatus.ACTIVE;
    }
    
    public User(String idCard, String password, String name) {
        this.id = generateId();
        this.idCard = idCard;
        this.password = password;
        this.name = name;
        this.createTime = LocalDateTime.now();
        this.status = UserStatus.ACTIVE;
    }
    
    // 生成用户ID
    private String generateId() {
        return "U" + System.currentTimeMillis();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public LocalDateTime getLastLoginTime() { return lastLoginTime; }
    public void setLastLoginTime(LocalDateTime lastLoginTime) { this.lastLoginTime = lastLoginTime; }
    
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
}
