package com.example.depu.manager;

public class UserAgreement {
    /**
     * 玩家唯一标识符
     */
    private String playerId;
    
    /**
     * 是否接受用户协议
     */
    private boolean accepted;
    
    /**
     * 接受时间（毫秒）
     */
    private long acceptTime;

    public UserAgreement(String playerId, boolean accepted) {
        this.playerId = playerId;
        this.accepted = accepted;
        this.acceptTime = System.currentTimeMillis();
    }

    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }

    public boolean isAccepted() { return accepted; }
    public void setAccepted(boolean accepted) { this.accepted = accepted; }

    public long getAcceptTime() { return acceptTime; }
    public void setAcceptTime(long acceptTime) { this.acceptTime = acceptTime; }
}
