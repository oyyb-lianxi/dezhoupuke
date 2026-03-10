package com.example.depu.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Table implements java.io.Serializable {
    /**
     * 桌台唯一标识符
     */
    private String id;
    
    /**
     * 桌台最大玩家数量
     */
    private int maxPlayers;
    
    /**
     * 自动开启游戏的最小玩家数
     */
    private int minPlayersToStart;
    
    /**
     * 当前在桌台游戏的玩家列表
     */
    private List<Player> players;
    
    /**
     * 当前在桌台观战的玩家列表
     */
    private List<Player> spectators;
    
    /**
     * 当前正在进行的游戏
     */
    private Game currentGame;
    
    /**
     * 小盲注金额
     */
    private int smallBlind;
    
    /**
     * 大盲注金额
     */
    private int bigBlind;
    
    /**
     * 盲注上限
     */
    private int blindLimit;
    
    /**
     * 涨盲周期（分钟）
     */
    private int blindIncreaseInterval;
    
    /**
     * 上次涨盲的时间
     */
    private LocalDateTime lastBlindIncreaseTime;
    
    /**
     * 桌台开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 单场比赛时长（分钟）
     */
    private int tournamentDuration;
    
    /**
     * 桌台是否正在运行
     */
    private boolean isRunning;
    
    /**
     * 桌台是否暂停
     */
    private boolean isPaused;
    
    /**
     * 当前底池金额
     */
    private int pot;
    
    /**
     * 当前下注金额
     */
    private int currentBet;
    
    /**
     * 桌台状态
     */
    private TableStatus status;

    public enum TableStatus {
        WAITING, RUNNING, PAUSED, ENDED
    }

    public Table(String id, int maxPlayers, int minPlayersToStart, int smallBlind, int bigBlind, int blindLimit) {
        this.id = id;
        this.maxPlayers = maxPlayers;
        this.minPlayersToStart = minPlayersToStart;
        this.players = new ArrayList<>();
        this.spectators = new ArrayList<>();
        this.smallBlind = smallBlind;
        this.bigBlind = bigBlind;
        this.blindLimit = blindLimit;
        this.blindIncreaseInterval = 10; // 固定10分钟
        this.tournamentDuration = 60; // 固定60分钟
        this.status = TableStatus.WAITING;
        this.isRunning = false;
        this.isPaused = false;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }

    public int getMinPlayersToStart() { return minPlayersToStart; }
    public void setMinPlayersToStart(int minPlayersToStart) { this.minPlayersToStart = minPlayersToStart; }

    public List<Player> getPlayers() { return players; }
    public void setPlayers(List<Player> players) { this.players = players; }

    public List<Player> getSpectators() { return spectators; }
    public void setSpectators(List<Player> spectators) { this.spectators = spectators; }

    public Game getCurrentGame() { return currentGame; }
    public void setCurrentGame(Game currentGame) { this.currentGame = currentGame; }

    public int getSmallBlind() { return smallBlind; }
    public void setSmallBlind(int smallBlind) { this.smallBlind = smallBlind; }

    public int getBigBlind() { return bigBlind; }
    public void setBigBlind(int bigBlind) { this.bigBlind = bigBlind; }

    public int getBlindLimit() { return blindLimit; }
    public void setBlindLimit(int blindLimit) { this.blindLimit = blindLimit; }

    public int getBlindIncreaseInterval() { return blindIncreaseInterval; }
    public void setBlindIncreaseInterval(int blindIncreaseInterval) { this.blindIncreaseInterval = blindIncreaseInterval; }

    public LocalDateTime getLastBlindIncreaseTime() { return lastBlindIncreaseTime; }
    public void setLastBlindIncreaseTime(LocalDateTime lastBlindIncreaseTime) { this.lastBlindIncreaseTime = lastBlindIncreaseTime; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public int getTournamentDuration() { return tournamentDuration; }
    public void setTournamentDuration(int tournamentDuration) { this.tournamentDuration = tournamentDuration; }

    public boolean isRunning() { return isRunning; }
    public void setRunning(boolean running) { isRunning = running; }

    public boolean isPaused() { return isPaused; }
    public void setPaused(boolean paused) { isPaused = paused; }

    public int getPot() { return pot; }
    public void setPot(int pot) { this.pot = pot; }

    public int getCurrentBet() { return currentBet; }
    public void setCurrentBet(int currentBet) { this.currentBet = currentBet; }

    public TableStatus getStatus() { return status; }
    public void setStatus(TableStatus status) { this.status = status; }

    // 玩家入座
    public boolean addPlayer(Player player) {
        if (players.size() < maxPlayers && !isPlayerAtTable(player.getId())) {
            player.setTablePoints(0);
            player.setPendingPoints(0);
            player.setSpectating(false);
            players.add(player);
            checkAndStartGame();
            return true;
        }
        return false;
    }

    // 玩家离座
    public boolean removePlayer(String playerId) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getId().equals(playerId)) {
                players.remove(i);
                checkTableStatus();
                return true;
            }
        }
        return false;
    }

    // 玩家进入观战状态
    public void moveToSpectators(Player player) {
        players.remove(player);
        player.setSpectating(true);
        spectators.add(player);
        checkTableStatus();
    }

    // 玩家从观战状态回到游戏
    public boolean moveFromSpectators(Player player, int buyIn) {
        if (players.size() < maxPlayers && player.getWarehousePoints() >= buyIn) {
            spectators.remove(player);
            player.resetTablePoints(buyIn);
            player.setSpectating(false);
            players.add(player);
            checkAndStartGame();
            return true;
        }
        return false;
    }

    // 检查并启动游戏
    private void checkAndStartGame() {
        if (!isRunning && players.size() >= minPlayersToStart) {
            startGame();
        }
    }

    // 启动游戏
    public void startGame() {
        isRunning = true;
        status = TableStatus.RUNNING;
        startTime = LocalDateTime.now();
        lastBlindIncreaseTime = startTime;
        startNewGame();
    }

    // 开始新的牌局
    public void startNewGame() {
        // 应用待生效补分
        for (Player player : players) {
            player.applyPendingPoints();
        }
        currentGame = new Game(this);
        currentGame.start();
    }

    // 检查桌台状态
    private void checkTableStatus() {
        if (players.size() == 0) {
            status = TableStatus.WAITING;
            isRunning = false;
            currentGame = null;
        } else if (players.size() == 1) {
            status = TableStatus.PAUSED;
            isPaused = true;
        } else if (players.size() >= minPlayersToStart && !isRunning) {
            startGame();
        }
    }

    // 检查是否需要涨盲
    public void checkBlindIncrease() {
        if (isRunning && !isPaused) {
            LocalDateTime now = LocalDateTime.now();
            if (lastBlindIncreaseTime.plusMinutes(blindIncreaseInterval).isBefore(now)) {
                increaseBlinds();
                lastBlindIncreaseTime = now;
            }
        }
    }

    // 涨盲
    private void increaseBlinds() {
        if (bigBlind < blindLimit) {
            smallBlind *= 2;
            bigBlind *= 2;
            // 确保不超过盲注上限
            if (bigBlind > blindLimit) {
                bigBlind = blindLimit;
                smallBlind = blindLimit / 2;
            }
        }
    }

    // 检查玩家是否在桌台
    private boolean isPlayerAtTable(String playerId) {
        for (Player player : players) {
            if (player.getId().equals(playerId)) {
                return true;
            }
        }
        for (Player spectator : spectators) {
            if (spectator.getId().equals(playerId)) {
                return true;
            }
        }
        return false;
    }

    // 暂停桌台
    public void pause() {
        isPaused = true;
        status = TableStatus.PAUSED;
    }

    // 恢复桌台
    public void resume() {
        isPaused = false;
        status = TableStatus.RUNNING;
    }

    // 强制结束桌台
    public void forceEnd() {
        isRunning = false;
        status = TableStatus.ENDED;
        // 结算当前牌局
        if (currentGame != null) {
            currentGame.end();
        }
        // 计算排名并发放经验值
        calculateRankingAndExperience();
    }

    // 计算排名并发放经验值
    private void calculateRankingAndExperience() {
        // 按桌面积分排序
        players.sort((p1, p2) -> Integer.compare(p2.getTablePoints(), p1.getTablePoints()));
        
        int playerCount = players.size();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            int rank = i + 1;
            int experienceChange = 0;
            
            // 根据排名计算经验值变化
            switch (rank) {
                case 1:
                    experienceChange = 10;
                    break;
                case 2:
                    if (playerCount >= 3) experienceChange = 6;
                    break;
                case 3:
                    if (playerCount >= 4) experienceChange = 3;
                    break;
                case 4:
                    if (playerCount >= 5) experienceChange = 1;
                    break;
                case 5:
                    if (playerCount >= 5) experienceChange = 0;
                    break;
                case 6:
                    if (playerCount >= 6) experienceChange = -1;
                    break;
                case 7:
                    if (playerCount >= 7) experienceChange = -3;
                    break;
                case 8:
                    if (playerCount >= 8) experienceChange = -6;
                    break;
                case 9:
                    if (playerCount >= 9) experienceChange = -10;
                    break;
            }
            
            // 更新经验值，确保不低于0
            int newExperience = player.getLevelExperience() + experienceChange;
            player.setLevelExperience(Math.max(newExperience, 0));
            player.calculateLevel();
            
            // 发放总积分奖励（简单实现，实际可根据规则调整）
            if (rank == 1) {
                player.setTotalPoints(player.getTotalPoints() + 100);
            } else if (rank == 2) {
                player.setTotalPoints(player.getTotalPoints() + 50);
            } else if (rank == 3) {
                player.setTotalPoints(player.getTotalPoints() + 25);
            }
        }
    }

    // 检查比赛是否结束
    public boolean isTournamentEnded() {
        if (!isRunning) return true;
        if (startTime.plusMinutes(tournamentDuration).isBefore(LocalDateTime.now())) {
            forceEnd();
            return true;
        }
        return false;
    }
}