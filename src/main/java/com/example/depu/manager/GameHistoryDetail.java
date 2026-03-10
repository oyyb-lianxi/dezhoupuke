package com.example.depu.manager;

import com.example.depu.model.Card;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GameHistoryDetail {
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
     * 公共牌列表
     */
    private List<Card> communityCards;
    
    /**
     * 玩家手牌列表
     */
    private List<Card> playerCards;
    
    /**
     * 对手牌信息列表
     */
    private List<OpponentCardInfo> opponentCards;
    
    /**
     * 下注轮次列表
     */
    private List<BettingRound> bettingRounds;
    
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

    public GameHistoryDetail(String id, String tableId, String gameId) {
        this.id = id;
        this.tableId = tableId;
        this.gameId = gameId;
        this.startTime = LocalDateTime.now();
        this.communityCards = new ArrayList<>();
        this.playerCards = new ArrayList<>();
        this.opponentCards = new ArrayList<>();
        this.bettingRounds = new ArrayList<>();
    }

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

    public List<Card> getCommunityCards() { return communityCards; }
    public void setCommunityCards(List<Card> communityCards) { this.communityCards = communityCards; }

    public List<Card> getPlayerCards() { return playerCards; }
    public void setPlayerCards(List<Card> playerCards) { this.playerCards = playerCards; }

    public List<OpponentCardInfo> getOpponentCards() { return opponentCards; }
    public void setOpponentCards(List<OpponentCardInfo> opponentCards) { this.opponentCards = opponentCards; }

    public List<BettingRound> getBettingRounds() { return bettingRounds; }
    public void setBettingRounds(List<BettingRound> bettingRounds) { this.bettingRounds = bettingRounds; }

    public int getPot() { return pot; }
    public void setPot(int pot) { this.pot = pot; }

    public String getWinnerId() { return winnerId; }
    public void setWinnerId(String winnerId) { this.winnerId = winnerId; }

    public int getPlayerResult() { return playerResult; }
    public void setPlayerResult(int playerResult) { this.playerResult = playerResult; }
}
