package com.example.depu.model;

import java.time.LocalDateTime;
import java.util.List;

public class Player implements java.io.Serializable {
    /**
     * 玩家唯一ID
     */
    private String id;
    
    /**
     * 玩家昵称
     */
    private String nickname;
    
    /**
     * 玩家头像
     */
    private String avatar;
    
    /**
     * 仓库积分（存储在仓库中的积分）
     */
    private int warehousePoints; // 仓库积分
    
    /**
     * 桌面积分（当前在桌台上的积分）
     */
    private int tablePoints; // 桌面积分
    
    /**
     * 待生效补分（下局生效的补分）
     */
    private int pendingPoints; // 待生效补分
    
    /**
     * 等级经验值
     */
    private int levelExperience; // 等级经验值
    
    /**
     * 总积分（仓库积分 + 桌面积分）
     */
    private int totalPoints; // 总积分
    
    /**
     * 玩家等级
     */
    private int level; // 玩家等级
    
    /**
     * 在线状态
     */
    private boolean isOnline;
    
    /**
     * 观战状态
     */
    private boolean isSpectating; // 观战状态
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    
    /**
     * 注册时间
     */
    private LocalDateTime registrationTime;
    
    /**
     * 实名验证状态
     */
    private boolean isVerified; // 实名验证状态
    
    /**
     * 年龄
     */
    private int age; // 年龄
    
    /**
     * IP地址
     */
    private String ipAddress;
    
    /**
     * 设备信息
     */
    private String deviceInfo;
    
    /**
     * 关联的用户ID
     */
    private String userId;
    
    /**
     * 手牌
     */
    private List<Card> holeCards;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public int getWarehousePoints() { return warehousePoints; }
    public void setWarehousePoints(int warehousePoints) { this.warehousePoints = warehousePoints; }

    public int getTablePoints() { return tablePoints; }
    public void setTablePoints(int tablePoints) { this.tablePoints = tablePoints; }

    public int getPendingPoints() { return pendingPoints; }
    public void setPendingPoints(int pendingPoints) { this.pendingPoints = pendingPoints; }

    public int getLevelExperience() { return levelExperience; }
    public void setLevelExperience(int levelExperience) { this.levelExperience = levelExperience; }

    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public boolean isOnline() { return isOnline; }
    public void setOnline(boolean online) { isOnline = online; }

    public boolean isSpectating() { return isSpectating; }
    public void setSpectating(boolean spectating) { isSpectating = spectating; }

    public LocalDateTime getLastLoginTime() { return lastLoginTime; }
    public void setLastLoginTime(LocalDateTime lastLoginTime) { this.lastLoginTime = lastLoginTime; }

    public LocalDateTime getRegistrationTime() { return registrationTime; }
    public void setRegistrationTime(LocalDateTime registrationTime) { this.registrationTime = registrationTime; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<Card> getHoleCards() { return holeCards; }
    public void setHoleCards(List<Card> holeCards) { this.holeCards = holeCards; }

    // 计算玩家等级
    public void calculateLevel() {
        if (levelExperience >= 451) {
            level = 10; // 至尊
        } else if (levelExperience >= 361) {
            level = 9; // 王者
        } else if (levelExperience >= 281) {
            level = 8; // 传奇
        } else if (levelExperience >= 211) {
            level = 7; // 宗师
        } else if (levelExperience >= 151) {
            level = 6; // 大师
        } else if (levelExperience >= 101) {
            level = 5; // 专家
        } else if (levelExperience >= 61) {
            level = 4; // 高手
        } else if (levelExperience >= 31) {
            level = 3; // 熟手
        } else if (levelExperience >= 11) {
            level = 2; // 学徒
        } else {
            level = 1; // 新手
        }
    }

    // 补分操作
    public boolean addPendingPoints(int points) {
        if (warehousePoints >= points) {
            warehousePoints -= points;
            pendingPoints += points;
            return true;
        }
        return false;
    }

    // 应用待生效补分
    public void applyPendingPoints() {
        tablePoints += pendingPoints;
        pendingPoints = 0;
    }

    // 检查是否被淘汰
    public boolean isEliminated() {
        return tablePoints <= 0;
    }

    // 重置桌面积分
    public void resetTablePoints(int buyIn) {
        if (warehousePoints >= buyIn) {
            warehousePoints -= buyIn;
            tablePoints = buyIn;
            pendingPoints = 0;
        }
    }

    // 清空手牌
    public void clearHoleCards() {
        if (holeCards != null) {
            holeCards.clear();
        }
    }
}