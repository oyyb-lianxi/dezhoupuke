package com.example.depu.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Game implements java.io.Serializable {
    private static final Logger logger = LoggerFactory.getLogger(Game.class);
    /**
     * 游戏唯一标识符
     */
    private String id;
    
    /**
     * 游戏所属的桌台
     */
    private Table table;
    
    /**
     * 参与游戏的玩家列表
     */
    private List<Player> players;
    
    /**
     * 公共牌列表
     */
    private List<Card> communityCards;
    
    /**
     * 玩家动作列表
     */
    private List<PlayerAction> actions;
    
    /**
     * 游戏状态
     */
    private GameState state;
    
    /**
     * 底池金额
     */
    private int pot;
    
    /**
     * 边池列表
     */
    private List<SidePot> sidePots;
    
    /**
     * 当前下注金额
     */
    private int currentBet;
    
    /**
     * 庄家位置
     */
    private int dealerPosition;
    
    /**
     * 当前玩家索引
     */
    private int currentPlayerIndex;
    
    /**
     * 游戏轮次
     */
    private int round;
    
    /**
     * 获胜者列表
     */
    private List<Player> winners;
    
    /**
     * 游戏是否完成
     */
    private boolean isComplete;
    
    /**
     * 牌堆
     */
    private List<Card> deck;

    public enum GameState {
        PREFLOP, FLOP, TURN, RIVER, SHOWDOWN, COMPLETED
    }

    public Game(Table table) {
        this.id = generateId();
        this.table = table;
        this.players = new ArrayList<>(table.getPlayers());
        this.communityCards = new ArrayList<>();
        this.actions = new ArrayList<>();
        this.state = GameState.PREFLOP;
        this.pot = 0;
        this.sidePots = new ArrayList<>();
        this.currentBet = 0;
        this.dealerPosition = new Random().nextInt(players.size());
        this.currentPlayerIndex = (dealerPosition + 1) % players.size();
        this.round = 0;
        this.winners = new ArrayList<>();
        this.isComplete = false;
        this.deck = createDeck();
        shuffleDeck();
    }

    // 边池类
    private static class SidePot {
        private int amount;
        private List<Player> eligiblePlayers;

        public SidePot(int amount, List<Player> eligiblePlayers) {
            this.amount = amount;
            this.eligiblePlayers = eligiblePlayers;
        }

        public int getAmount() { return amount; }
        public List<Player> getEligiblePlayers() { return eligiblePlayers; }
    }

    // 牌型评估结果
    private static class HandEvaluation {
        private HandRank rank;
        private List<Card> bestHand;
        private int[] kickers;

        public HandEvaluation(HandRank rank, List<Card> bestHand, int[] kickers) {
            this.rank = rank;
            this.bestHand = bestHand;
            this.kickers = kickers;
        }

        public HandRank getRank() { return rank; }
        public List<Card> getBestHand() { return bestHand; }
        public int[] getKickers() { return kickers; }
    }

    // 牌型枚举
    public enum HandRank {
        HIGH_CARD(0),
        PAIR(1),
        TWO_PAIR(2),
        THREE_OF_A_KIND(3),
        STRAIGHT(4),
        FLUSH(5),
        FULL_HOUSE(6),
        FOUR_OF_A_KIND(7),
        STRAIGHT_FLUSH(8),
        ROYAL_FLUSH(9);

        private final int value;

        HandRank(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Table getTable() { return table; }
    public void setTable(Table table) { this.table = table; }

    public List<Player> getPlayers() { return players; }
    public void setPlayers(List<Player> players) { this.players = players; }

    public List<Card> getCommunityCards() { return communityCards; }
    public void setCommunityCards(List<Card> communityCards) { this.communityCards = communityCards; }

    public List<PlayerAction> getActions() { return actions; }
    public void setActions(List<PlayerAction> actions) { this.actions = actions; }

    public GameState getState() { return state; }
    public void setState(GameState state) { this.state = state; }

    public int getPot() { return pot; }
    public void setPot(int pot) { this.pot = pot; }

    public int getCurrentBet() { return currentBet; }
    public void setCurrentBet(int currentBet) { this.currentBet = currentBet; }

    public int getDealerPosition() { return dealerPosition; }
    public void setDealerPosition(int dealerPosition) { this.dealerPosition = dealerPosition; }

    public int getCurrentPlayerIndex() { return currentPlayerIndex; }
    public void setCurrentPlayerIndex(int currentPlayerIndex) { this.currentPlayerIndex = currentPlayerIndex; }

    public int getRound() { return round; }
    public void setRound(int round) { this.round = round; }

    public List<Player> getWinners() { return winners; }
    public void setWinners(List<Player> winners) { this.winners = winners; }

    public boolean isComplete() { return isComplete; }
    public void setComplete(boolean complete) { isComplete = complete; }

    // 生成游戏ID
    private String generateId() {
        return "G" + System.currentTimeMillis();
    }

    // 创建牌堆
    private List<Card> createDeck() {
        List<Card> deck = new ArrayList<>();
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                deck.add(new Card(suit, rank));
            }
        }
        return deck;
    }

    // 洗牌
    private void shuffleDeck() {
        Collections.shuffle(deck);
        logger.info("牌堆已洗牌");
    }

    // 发牌
    private Card dealCard() {
        if (deck.isEmpty()) {
            deck = createDeck();
            shuffleDeck();
        }
        return deck.remove(0);
    }

    // 开始游戏
    public void start() {
        logger.info("游戏开始: ID={}", id);
        // 发放手牌
        dealHoleCards();
        // 收取盲注
        collectBlinds();
        // 开始翻牌前 betting round
        startBettingRound();
    }

    // 发放手牌
    private void dealHoleCards() {
        logger.info("发放手牌给 {} 名玩家", players.size());
        // 为每个玩家发放两张牌
        for (Player player : players) {
            List<Card> holeCards = new ArrayList<>();
            holeCards.add(dealCard());
            holeCards.add(dealCard());
            player.setHoleCards(holeCards);
            logger.info("玩家 {} 获得手牌: {} 和 {}", player.getNickname(), holeCards.get(0), holeCards.get(1));
        }
    }

    // 收取盲注
    private void collectBlinds() {
        int smallBlindIndex = (dealerPosition + 1) % players.size();
        int bigBlindIndex = (dealerPosition + 2) % players.size();

        Player smallBlindPlayer = players.get(smallBlindIndex);
        Player bigBlindPlayer = players.get(bigBlindIndex);

        // 收取小盲注
        int smallBlindAmount = table.getSmallBlind();
        if (smallBlindPlayer.getTablePoints() >= smallBlindAmount) {
            smallBlindPlayer.setTablePoints(smallBlindPlayer.getTablePoints() - smallBlindAmount);
            pot += smallBlindAmount;
            actions.add(new PlayerAction(smallBlindPlayer, PlayerAction.ActionType.SMALL_BLIND, smallBlindAmount));
            logger.info("玩家 {} 支付小盲注: {}", smallBlindPlayer.getNickname(), smallBlindAmount);
        }

        // 收取大盲注
        int bigBlindAmount = table.getBigBlind();
        if (bigBlindPlayer.getTablePoints() >= bigBlindAmount) {
            bigBlindPlayer.setTablePoints(bigBlindPlayer.getTablePoints() - bigBlindAmount);
            pot += bigBlindAmount;
            currentBet = bigBlindAmount;
            actions.add(new PlayerAction(bigBlindPlayer, PlayerAction.ActionType.BIG_BLIND, bigBlindAmount));
            logger.info("玩家 {} 支付大盲注: {}", bigBlindPlayer.getNickname(), bigBlindAmount);
        }

        // 设置当前玩家为小盲注之后的玩家
        currentPlayerIndex = (bigBlindIndex + 1) % players.size();
    }

    // 开始下注轮次
    private void startBettingRound() {
        logger.info("开始下注轮次: {}", state.name());
        // 重置当前下注
        currentBet = 0;
        // 实际实现需要处理玩家操作、超时等
    }

    // 处理玩家操作
    public void handlePlayerAction(Player player, PlayerAction.ActionType actionType, int amount) {
        if (!isPlayerTurn(player)) {
            return;
        }

        switch (actionType) {
            case FOLD:
                handleFold(player);
                break;
            case CHECK:
                handleCheck(player);
                break;
            case CALL:
                handleCall(player, amount);
                break;
            case BET:
                handleBet(player, amount);
                break;
            case RAISE:
                handleRaise(player, amount);
                break;
            case ALL_IN:
                handleAllIn(player);
                break;
        }

        // 检查是否需要进入下一轮
        checkNextRound();
    }

    // 检查是否是玩家的回合
    private boolean isPlayerTurn(Player player) {
        return players.get(currentPlayerIndex).getId().equals(player.getId());
    }

    // 处理弃牌
    private void handleFold(Player player) {
        actions.add(new PlayerAction(player, PlayerAction.ActionType.FOLD, 0));
        players.remove(player);
        logger.info("玩家 {} 弃牌", player.getNickname());
        moveToNextPlayer();
    }

    // 处理过牌
    private void handleCheck(Player player) {
        actions.add(new PlayerAction(player, PlayerAction.ActionType.CHECK, 0));
        logger.info("玩家 {} 过牌", player.getNickname());
        moveToNextPlayer();
    }

    // 处理跟注
    private void handleCall(Player player, int amount) {
        int callAmount = Math.min(amount, player.getTablePoints());
        player.setTablePoints(player.getTablePoints() - callAmount);
        pot += callAmount;
        actions.add(new PlayerAction(player, PlayerAction.ActionType.CALL, callAmount));
        logger.info("玩家 {} 跟注: {}", player.getNickname(), callAmount);
        moveToNextPlayer();
    }

    // 处理下注
    private void handleBet(Player player, int amount) {
        int betAmount = Math.min(amount, player.getTablePoints());
        player.setTablePoints(player.getTablePoints() - betAmount);
        pot += betAmount;
        currentBet = betAmount;
        actions.add(new PlayerAction(player, PlayerAction.ActionType.BET, betAmount));
        logger.info("玩家 {} 下注: {}", player.getNickname(), betAmount);
        moveToNextPlayer();
    }

    // 处理加注
    private void handleRaise(Player player, int amount) {
        int raiseAmount = Math.min(amount, player.getTablePoints());
        player.setTablePoints(player.getTablePoints() - raiseAmount);
        pot += raiseAmount;
        currentBet = raiseAmount;
        actions.add(new PlayerAction(player, PlayerAction.ActionType.RAISE, raiseAmount));
        logger.info("玩家 {} 加注: {}", player.getNickname(), raiseAmount);
        moveToNextPlayer();
    }

    // 处理全下
    private void handleAllIn(Player player) {
        int allInAmount = player.getTablePoints();
        player.setTablePoints(0);
        pot += allInAmount;
        currentBet = Math.max(currentBet, allInAmount);
        actions.add(new PlayerAction(player, PlayerAction.ActionType.ALL_IN, allInAmount));
        logger.info("玩家 {} 全下: {}", player.getNickname(), allInAmount);
        moveToNextPlayer();
    }

    // 移动到下一个玩家
    private void moveToNextPlayer() {
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } while (players.get(currentPlayerIndex).isSpectating());
    }

    // 检查是否需要进入下一轮
    private void checkNextRound() {
        // 检查是否所有玩家都已行动或只剩下一个玩家
        if (players.size() <= 1) {
            // 只有一个玩家，直接获胜
            if (!players.isEmpty()) {
                winners.add(players.get(0));
                logger.info("玩家 {} 获胜（唯一剩余玩家）", players.get(0).getNickname());
            }
            state = GameState.COMPLETED;
            end();
            return;
        }

        // 检查是否所有玩家都已完成当前轮次的下注
        boolean allPlayersActed = true;
        // 实际实现需要更复杂的逻辑来判断是否所有玩家都已行动

        if (allPlayersActed) {
            // 进入下一个游戏阶段
            switch (state) {
                case PREFLOP:
                    state = GameState.FLOP;
                    dealFlop();
                    startBettingRound();
                    break;
                case FLOP:
                    state = GameState.TURN;
                    dealTurn();
                    startBettingRound();
                    break;
                case TURN:
                    state = GameState.RIVER;
                    dealRiver();
                    startBettingRound();
                    break;
                case RIVER:
                    state = GameState.SHOWDOWN;
                    determineWinner();
                    break;
                case SHOWDOWN:
                    state = GameState.COMPLETED;
                    end();
                    break;
            }
        }
    }

    // 发放翻牌
    private void dealFlop() {
        // 发放三张公共牌
        logger.info("发放翻牌");
        communityCards.clear();
        for (int i = 0; i < 3; i++) {
            Card card = dealCard();
            communityCards.add(card);
            logger.info("公共牌 {}: {}", i + 1, card);
        }
    }

    // 发放转牌
    private void dealTurn() {
        // 发放第四张公共牌
        logger.info("发放转牌");
        Card card = dealCard();
        communityCards.add(card);
        logger.info("转牌: {}", card);
    }

    // 发放河牌
    private void dealRiver() {
        // 发放第五张公共牌
        logger.info("发放河牌");
        Card card = dealCard();
        communityCards.add(card);
        logger.info("河牌: {}", card);
    }

    // 确定获胜者
    private void determineWinner() {
        logger.info("进入摊牌阶段，确定获胜者");
        
        // 创建边池
        createSidePots();
        
        // 评估每个玩家的牌型
        List<PlayerHand> playerHands = new ArrayList<>();
        for (Player player : players) {
            List<Card> allCards = new ArrayList<>(player.getHoleCards());
            allCards.addAll(communityCards);
            HandEvaluation evaluation = evaluateHand(allCards);
            playerHands.add(new PlayerHand(player, evaluation));
            logger.info("玩家 {} 的牌型: {}, 最佳牌: {}", player.getNickname(), evaluation.getRank(), evaluation.getBestHand());
        }
        
        // 处理主池和边池的分配
        if (!sidePots.isEmpty()) {
            for (SidePot sidePot : sidePots) {
                List<PlayerHand> eligibleHands = new ArrayList<>();
                for (PlayerHand ph : playerHands) {
                    if (sidePot.getEligiblePlayers().contains(ph.player)) {
                        eligibleHands.add(ph);
                    }
                }
                awardPot(sidePot.getAmount(), eligibleHands);
            }
        } else {
            // 只有主池
            awardPot(pot, playerHands);
        }
    }

    // 创建边池
    private void createSidePots() {
        // 实际实现需要根据玩家的下注情况创建边池
        // 这里简化处理，只使用主池
    }

    // 评估手牌
    private HandEvaluation evaluateHand(List<Card> cards) {
        // 实际实现需要完整的牌型评估逻辑
        // 这里简化处理，返回高牌
        List<Card> sortedCards = new ArrayList<>(cards);
        sortedCards.sort(Comparator.comparingInt(c -> c.getRankValue()));
        Collections.reverse(sortedCards);
        
        return new HandEvaluation(HandRank.HIGH_CARD, sortedCards.subList(0, 5), 
            new int[]{sortedCards.get(0).getRankValue(), sortedCards.get(1).getRankValue(), 
                      sortedCards.get(2).getRankValue(), sortedCards.get(3).getRankValue(), 
                      sortedCards.get(4).getRankValue()});
    }

    // 分配底池
    private void awardPot(int amount, List<PlayerHand> playerHands) {
        if (playerHands.isEmpty()) {
            return;
        }
        
        // 找出最佳牌型
        PlayerHand bestHand = playerHands.get(0);
        for (PlayerHand ph : playerHands) {
            if (compareHands(ph.evaluation, bestHand.evaluation) > 0) {
                bestHand = ph;
            }
        }
        
        // 找出所有持有最佳牌型的玩家
        List<Player> potWinners = new ArrayList<>();
        for (PlayerHand ph : playerHands) {
            if (compareHands(ph.evaluation, bestHand.evaluation) == 0) {
                potWinners.add(ph.player);
            }
        }
        
        // 分配底池
        int splitAmount = amount / potWinners.size();
        for (Player winner : potWinners) {
            winner.setTablePoints(winner.getTablePoints() + splitAmount);
            this.winners.add(winner);
            logger.info("玩家 {} 赢得 {} 积分", winner.getNickname(), splitAmount);
        }
    }

    // 比较两个牌型
    private int compareHands(HandEvaluation hand1, HandEvaluation hand2) {
        // 比较牌型等级
        if (hand1.getRank().getValue() != hand2.getRank().getValue()) {
            return hand1.getRank().getValue() - hand2.getRank().getValue();
        }
        
        // 比较 kickers
        int[] kickers1 = hand1.getKickers();
        int[] kickers2 = hand2.getKickers();
        for (int i = 0; i < kickers1.length; i++) {
            if (kickers1[i] != kickers2[i]) {
                return kickers1[i] - kickers2[i];
            }
        }
        
        return 0; // 牌型相同
    }

    // 玩家手牌类
    private static class PlayerHand {
        private Player player;
        private HandEvaluation evaluation;

        public PlayerHand(Player player, HandEvaluation evaluation) {
            this.player = player;
            this.evaluation = evaluation;
        }
    }

    // 结束游戏
    public void end() {
        isComplete = true;
        logger.info("游戏结束: ID={}", id);
        // 检查玩家是否被淘汰
        checkEliminations();
        // 通知桌台开始新的牌局
        table.startNewGame();
    }

    // 检查玩家是否被淘汰
    private void checkEliminations() {
        List<Player> eliminatedPlayers = new ArrayList<>();
        for (Player player : players) {
            if (player.isEliminated()) {
                eliminatedPlayers.add(player);
            }
        }
        for (Player player : eliminatedPlayers) {
            table.moveToSpectators(player);
            logger.info("玩家 {} 被淘汰", player.getNickname());
        }
    }

    // 处理玩家超时
    public void handlePlayerTimeout(Player player) {
        if (currentBet == 0) {
            // 无需跟注，自动过牌
            handleCheck(player);
        } else {
            // 需要跟注，自动弃牌
            handleFold(player);
        }
    }
}