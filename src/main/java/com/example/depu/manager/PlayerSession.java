package com.example.depu.manager;

public class PlayerSession {
    /**
     * 会话唯一标识符
     */
    private String sessionId;
    
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
    
    /**
     * 会话超时时间（1小时，单位：毫秒）
     */
    private static final long SESSION_TIMEOUT = 3600000;

    public PlayerSession(String sessionId, String playerId, String ipAddress, String deviceInfo) {
        this.sessionId = sessionId;
        this.playerId = playerId;
        this.ipAddress = ipAddress;
        this.deviceInfo = deviceInfo;
        this.createTime = System.currentTimeMillis();
        this.lastAccessTime = System.currentTimeMillis();
    }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

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

    public boolean isExpired() {
        return System.currentTimeMillis() - lastAccessTime > SESSION_TIMEOUT;
    }

    public void updateLastAccessTime() {
        lastAccessTime = System.currentTimeMillis();
    }
}
