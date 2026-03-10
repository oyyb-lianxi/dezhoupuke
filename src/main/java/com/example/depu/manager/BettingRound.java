package com.example.depu.manager;

import com.example.depu.model.PlayerAction;

import java.util.ArrayList;
import java.util.List;

public class BettingRound {
    /**
     * 下注轮次名称（如PREFLOP、FLOP、TURN、RIVER）
     */
    private String roundName;
    
    /**
     * 轮次中的玩家动作列表
     */
    private List<PlayerAction> actions;

    public BettingRound(String roundName) {
        this.roundName = roundName;
        this.actions = new ArrayList<>();
    }

    public String getRoundName() { return roundName; }
    public void setRoundName(String roundName) { this.roundName = roundName; }

    public List<PlayerAction> getActions() { return actions; }
    public void setActions(List<PlayerAction> actions) { this.actions = actions; }
}
