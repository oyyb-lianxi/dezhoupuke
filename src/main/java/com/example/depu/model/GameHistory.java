package com.example.depu.model;

import java.time.LocalDateTime;

public class GameHistory implements java.io.Serializable {
    /**
     * 游戏历史记录唯一标识符
     */
    private String id;
    
    /**
     * 桌台唯一标识符
     */
    private String tableId;
    
    /**
     * 游戏唯一标识符
     */
    private String gameId;
    
    /**
     * 游戏开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 游戏结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 底池金额
     */
    private int pot;
    
    /**
     * 获胜者唯一标识符
     */
    private String winnerId;
    
    /**
     * 玩家结果（正为赢，负为输）
     */
    private int playerResult;

    // 构造函数
    public GameHistory() {
    }
    
    public GameHistory(String id, String tableId, String gameId) {
        this.id = id;
        this.tableId = tableId;
        this.gameId = gameId;
        this.startTime = LocalDateTime.now();
        this.pot = 0;
        this.playerResult = 0;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTableId() { return tableId; }
    public void setTableId(String tableId) { this.tableId = tableId; }
    
    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public int getPot() { return pot; }
    public void setPot(int pot) { this.pot = pot; }
    
    public String getWinnerId() { return winnerId; }
    public void setWinnerId(String winnerId) { this.winnerId = winnerId; }
    
    public int getPlayerResult() { return playerResult; }
    public void setPlayerResult(int playerResult) { this.playerResult = playerResult; }
}
