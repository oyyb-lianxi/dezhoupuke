package com.example.depu.model;

import java.time.LocalDateTime;

public class ExchangeRequest {
    /**
     * 兑换请求唯一标识符
     */
    private String id;
    
    /**
     * 玩家唯一标识符
     */
    private String playerId;
    
    /**
     * 兑换物品唯一标识符
     */
    private String itemId;
    
    /**
     * 请求时间
     */
    private LocalDateTime requestTime;
    
    /**
     * 处理时间
     */
    private LocalDateTime processTime;
    
    /**
     * 请求状态（PENDING、APPROVED、REJECTED）
     */
    private String status;

    public ExchangeRequest(String id, String playerId, String itemId) {
        this.id = id;
        this.playerId = playerId;
        this.itemId = itemId;
        this.requestTime = LocalDateTime.now();
        this.status = "PENDING";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public LocalDateTime getRequestTime() { return requestTime; }
    public void setRequestTime(LocalDateTime requestTime) { this.requestTime = requestTime; }

    public LocalDateTime getProcessTime() { return processTime; }
    public void setProcessTime(LocalDateTime processTime) { this.processTime = processTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
