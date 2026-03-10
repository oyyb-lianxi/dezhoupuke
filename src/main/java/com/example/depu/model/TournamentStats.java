package com.example.depu.model;

public class TournamentStats {
    /**
     * 总玩家数
     */
    private int totalPlayers;
    
    /**
     * 活跃桌台数
     */
    private int activeTables;
    
    /**
     * 已完成桌台数
     */
    private int completedTables;
    
    /**
     * 总桌台数
     */
    private int totalTables;

    public TournamentStats(int totalPlayers, int activeTables, int completedTables, int totalTables) {
        this.totalPlayers = totalPlayers;
        this.activeTables = activeTables;
        this.completedTables = completedTables;
        this.totalTables = totalTables;
    }

    public int getTotalPlayers() { return totalPlayers; }
    public void setTotalPlayers(int totalPlayers) { this.totalPlayers = totalPlayers; }

    public int getActiveTables() { return activeTables; }
    public void setActiveTables(int activeTables) { this.activeTables = activeTables; }

    public int getCompletedTables() { return completedTables; }
    public void setCompletedTables(int completedTables) { this.completedTables = completedTables; }

    public int getTotalTables() { return totalTables; }
    public void setTotalTables(int totalTables) { this.totalTables = totalTables; }
}
