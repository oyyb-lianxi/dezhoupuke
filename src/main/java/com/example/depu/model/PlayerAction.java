package com.example.depu.model;

public class PlayerAction implements java.io.Serializable {
    /**
     * 执行动作的玩家
     */
    private Player player;
    
    /**
     * 动作类型
     */
    private ActionType actionType;
    
    /**
     * 动作金额（下注、跟注或加注的金额）
     */
    private int amount;

    public enum ActionType {
        FOLD, CHECK, CALL, BET, RAISE, ALL_IN, SMALL_BLIND, BIG_BLIND
    }

    public PlayerAction(Player player, ActionType actionType, int amount) {
        this.player = player;
        this.actionType = actionType;
        this.amount = amount;
    }

    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }

    public ActionType getActionType() { return actionType; }
    public void setActionType(ActionType actionType) { this.actionType = actionType; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
}
