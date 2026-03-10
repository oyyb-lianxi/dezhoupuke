package com.example.depu.model;

public class PlayerSession implements java.io.Serializable {
    /**
     * 会话唯一标识符
     */
    private String id;
    
    /**
     * 玩家唯一标识符
     */
    private String playerId;
    
    /**
     * IP地址
     */
    private String ipAddress;
    
    /**
     * 设备信息
     */
    private String deviceInfo;
    
    /**
     * 创建时间（毫秒）
     */
    private long createTime;
    
    /**
     * 最后访问时间（毫秒）
     */
    private long lastAccessTime;

    // 构造函数
    public PlayerSession() {
    }
    
    public PlayerSession(String id, String playerId, String ipAddress, String deviceInfo) {
        this.id = id;
        this.playerId = playerId;
        this.ipAddress = ipAddress;
        this.deviceInfo = deviceInfo;
        this.createTime = System.currentTimeMillis();
        this.lastAccessTime = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }
    
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
    
    public long getLastAccessTime() { return lastAccessTime; }
    public void setLastAccessTime(long lastAccessTime) { this.lastAccessTime = lastAccessTime; }
    
    // 检查会话是否过期
    public boolean isExpired() {
        // 会话有效期为24小时
        return System.currentTimeMillis() - lastAccessTime > 24 * 60 * 60 * 1000;
    }
}
