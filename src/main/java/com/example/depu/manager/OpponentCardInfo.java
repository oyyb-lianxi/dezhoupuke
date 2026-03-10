package com.example.depu.manager;

import com.example.depu.model.Card;

import java.util.List;

public class OpponentCardInfo {
    /**
     * 对手玩家唯一标识符
     */
    private String playerId;
    
    /**
     * 对手的牌列表
     */
    private List<Card> cards;
    
    /**
     * 牌是否已显示
     */
    private boolean shown;

    public OpponentCardInfo(String playerId, List<Card> cards, boolean shown) {
        this.playerId = playerId;
        this.cards = cards;
        this.shown = shown;
    }

    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }

    public List<Card> getCards() { return cards; }
    public void setCards(List<Card> cards) { this.cards = cards; }

    public boolean isShown() { return shown; }
    public void setShown(boolean shown) { this.shown = shown; }
}
