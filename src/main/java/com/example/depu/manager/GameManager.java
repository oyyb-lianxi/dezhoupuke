package com.example.depu.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.depu.model.Game;
import com.example.depu.model.Player;
import com.example.depu.model.PlayerAction;
import com.example.depu.model.Table;
import com.example.depu.model.Tournament;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {
    private static final Logger logger = LoggerFactory.getLogger(GameManager.class);
    private static GameManager instance;
    private ConcurrentHashMap<String, Timer> playerTimers;
    private ConcurrentHashMap<String, Long> playerActionTimes;

    private GameManager() {
        playerTimers = new ConcurrentHashMap<>();
        playerActionTimes = new ConcurrentHashMap<>();
    }

    public static synchronized GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    // 开始游戏逻辑
    public void startGame(Game game) {
        logger.info("游戏开始: 桌台={}", game.getTable().getId());
        game.start();
        // 启动游戏计时器
        startGameTimer(game);
    }

    // 处理玩家操作
    public void handlePlayerAction(Game game, Player player, PlayerAction.ActionType actionType, int amount) {
        logger.info("玩家操作: 玩家={}, 操作={}, 金额={}", player.getNickname(), actionType, amount);
        // 取消玩家的超时计时器
        cancelPlayerTimer(player.getId());
        // 处理玩家操作
        game.handlePlayerAction(player, actionType, amount);
        // 为下一个玩家设置计时器
        setPlayerTimer(game);
    }

    // 设置玩家操作计时器
    private void setPlayerTimer(Game game) {
        if (game.isComplete()) {
            return;
        }

        Player currentPlayer = game.getPlayers().get(game.getCurrentPlayerIndex());
        if (currentPlayer == null || currentPlayer.isSpectating()) {
            return;
        }

        // 取消之前的计时器
        cancelPlayerTimer(currentPlayer.getId());

        // 设置新的计时器
        Timer timer = new Timer();
        playerTimers.put(currentPlayer.getId(), timer);
        playerActionTimes.put(currentPlayer.getId(), System.currentTimeMillis());

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 处理玩家超时
                handlePlayerTimeout(game, currentPlayer);
            }
        }, 10000); // 10秒超时
    }

    // 取消玩家计时器
    private void cancelPlayerTimer(String playerId) {
        Timer timer = playerTimers.remove(playerId);
        if (timer != null) {
            timer.cancel();
        }
        playerActionTimes.remove(playerId);
    }

    // 处理玩家超时
    private void handlePlayerTimeout(Game game, Player player) {
        if (!game.isComplete()) {
            logger.warn("玩家超时: 玩家={}", player.getNickname());
            game.handlePlayerTimeout(player);
            // 为下一个玩家设置计时器
            setPlayerTimer(game);
        }
    }

    // 开始游戏计时器
    private void startGameTimer(Game game) {
        // 启动桌台的盲注计时器
        Table table = game.getTable();
        if (table != null) {
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (table.isRunning() && !table.isPaused()) {
                        table.checkBlindIncrease();
                    } else {
                        timer.cancel();
                    }
                }
            }, 0, 60000); // 每分钟检查一次
        }
    }

    // 检查并处理淘汰
    public void checkEliminations(Table table) {
        for (Player player : table.getPlayers()) {
            if (player.isEliminated()) {
                logger.info("玩家被淘汰: 玩家={}, 桌台={}", player.getNickname(), table.getId());
                table.moveToSpectators(player);
            }
        }
    }

    // 处理补分
    public boolean handleAddPoints(Player player, int points) {
        boolean success = player.addPendingPoints(points);
        if (success) {
            logger.info("补分成功: 玩家={}, 补分数额={}", player.getNickname(), points);
        } else {
            logger.warn("补分失败: 玩家={}, 补分数额={}", player.getNickname(), points);
        }
        return success;
    }

    // 应用待生效补分
    public void applyPendingPoints(Table table) {
        for (Player player : table.getPlayers()) {
            int pendingPoints = player.getPendingPoints();
            if (pendingPoints > 0) {
                player.applyPendingPoints();
                logger.info("应用补分: 玩家={}, 补分数额={}", player.getNickname(), pendingPoints);
            }
        }
    }

    // 处理玩家重新加入
    public boolean handleRejoin(Player player, Table table, int buyIn) {
        if (player.getWarehousePoints() >= buyIn) {
            boolean success = table.moveFromSpectators(player, buyIn);
            if (success) {
                logger.info("玩家重新加入: 玩家={}, 桌台={}, 买入={}", player.getNickname(), table.getId(), buyIn);
            }
            return success;
        }
        logger.warn("玩家重新加入失败: 玩家={}, 桌台={}, 买入={}, 仓库积分不足", player.getNickname(), table.getId(), buyIn);
        return false;
    }

    // 检查赛事状态
    public void checkTournamentStatus(Tournament tournament) {
        logger.info("检查赛事状态: 赛事={}", tournament.getName());
        tournament.checkStatus();
    }

    // 清理资源
    public void cleanup() {
        logger.info("清理游戏管理器资源");
        for (Timer timer : playerTimers.values()) {
            timer.cancel();
        }
        playerTimers.clear();
        playerActionTimes.clear();
    }
}