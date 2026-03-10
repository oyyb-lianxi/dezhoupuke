package com.example.depu.manager;

import com.example.depu.model.Player;
import com.example.depu.model.Table;
import com.example.depu.model.Tournament;
import com.example.depu.model.Game;
import com.example.depu.model.ExchangeItem;
import com.example.depu.model.ExchangeRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class AdminManager {
    private static final Logger logger = LoggerFactory.getLogger(AdminManager.class);
    private static AdminManager instance;
    private ConcurrentHashMap<String, Tournament> tournaments;
    private ConcurrentHashMap<String, Player> players;
    private ConcurrentHashMap<String, ExchangeItem> exchangeItems;
    private ConcurrentHashMap<String, ExchangeRequest> exchangeRequests;

    private AdminManager() {
        tournaments = new ConcurrentHashMap<>();
        players = new ConcurrentHashMap<>();
        exchangeItems = new ConcurrentHashMap<>();
        exchangeRequests = new ConcurrentHashMap<>();
    }

    public static synchronized AdminManager getInstance() {
        if (instance == null) {
            instance = new AdminManager();
        }
        return instance;
    }

    // 创建赛事
    public Tournament createTournament(String name, int maxTables, int smallBlind, int bigBlind, int blindLimit) {
        Tournament tournament = new Tournament(name, maxTables, smallBlind, bigBlind, blindLimit);
        tournaments.put(tournament.getId(), tournament);
        logger.info("创建赛事 - 名称: {}, 最大桌台: {}, 小盲注: {}, 大盲注: {}, 盲注上限: {}", 
                name, maxTables, smallBlind, bigBlind, blindLimit);
        return tournament;
    }

    // 开始赛事
    public void startTournament(String tournamentId) {
        Tournament tournament = tournaments.get(tournamentId);
        if (tournament != null) {
            tournament.start();
            logger.info("开始赛事 - ID: {}, 名称: {}", tournamentId, tournament.getName());
        }
    }

    // 暂停赛事
    public void pauseTournament(String tournamentId) {
        Tournament tournament = tournaments.get(tournamentId);
        if (tournament != null) {
            tournament.pause();
            logger.info("暂停赛事 - ID: {}, 名称: {}", tournamentId, tournament.getName());
        }
    }

    // 结束赛事
    public void endTournament(String tournamentId) {
        Tournament tournament = tournaments.get(tournamentId);
        if (tournament != null) {
            tournament.end();
            logger.info("结束赛事 - ID: {}, 名称: {}", tournamentId, tournament.getName());
        }
    }

    // 添加桌台
    public boolean addTable(String tournamentId, String tableId, int maxPlayers, int minPlayersToStart) {
        Tournament tournament = tournaments.get(tournamentId);
        if (tournament != null) {
            boolean result = tournament.addTable(tableId, maxPlayers, minPlayersToStart);
            if (result) {
                logger.info("添加桌台 - 赛事: {}, 桌台: {}, 最大玩家: {}, 最小开始玩家: {}", 
                        tournamentId, tableId, maxPlayers, minPlayersToStart);
            }
            return result;
        }
        return false;
    }

    // 移除桌台
    public boolean removeTable(String tournamentId, String tableId) {
        Tournament tournament = tournaments.get(tournamentId);
        if (tournament != null) {
            boolean result = tournament.removeTable(tableId);
            if (result) {
                logger.info("移除桌台 - 赛事: {}, 桌台: {}", tournamentId, tableId);
            }
            return result;
        }
        return false;
    }

    // 强制结束桌台
    public void forceEndTable(String tournamentId, String tableId) {
        Tournament tournament = tournaments.get(tournamentId);
        if (tournament != null) {
            tournament.forceEndTable(tableId);
            logger.info("强制结束桌台 - 赛事: {}, 桌台: {}", tournamentId, tableId);
        }
    }

    // 管理玩家仓库积分
    public boolean manageWarehousePoints(String playerId, int amount, String reason) {
        Player player = players.get(playerId);
        if (player != null) {
            player.setWarehousePoints(player.getWarehousePoints() + amount);
            // 记录操作日志
            logOperation("WAREHOUSE_POINTS", playerId, amount, reason);
            logger.info("管理仓库积分 - 玩家: {}, 金额: {}, 原因: {}", playerId, amount, reason);
            return true;
        }
        return false;
    }

    // 管理玩家总积分
    public boolean manageTotalPoints(String playerId, int amount, String reason) {
        Player player = players.get(playerId);
        if (player != null) {
            player.setTotalPoints(player.getTotalPoints() + amount);
            // 记录操作日志
            logOperation("TOTAL_POINTS", playerId, amount, reason);
            logger.info("管理总积分 - 玩家: {}, 金额: {}, 原因: {}", playerId, amount, reason);
            return true;
        }
        return false;
    }

    // 管理玩家等级经验值
    public boolean manageLevelExperience(String playerId, int amount, String reason) {
        Player player = players.get(playerId);
        if (player != null) {
            int newExperience = player.getLevelExperience() + amount;
            player.setLevelExperience(Math.max(newExperience, 0));
            player.calculateLevel();
            // 记录操作日志
            logOperation("LEVEL_EXPERIENCE", playerId, amount, reason);
            logger.info("管理等级经验值 - 玩家: {}, 金额: {}, 原因: {}", playerId, amount, reason);
            return true;
        }
        return false;
    }

    // 管理兑换项目
    public void addExchangeItem(String itemId, String name, int points, int stock, String type) {
        ExchangeItem item = new ExchangeItem(itemId, name, points, stock, type);
        exchangeItems.put(itemId, item);
        logger.info("添加兑换物品 - ID: {}, 名称: {}, 积分: {}, 库存: {}, 类型: {}", 
                itemId, name, points, stock, type);
    }

    // 更新兑换项目库存
    public boolean updateExchangeItemStock(String itemId, int stock) {
        ExchangeItem item = exchangeItems.get(itemId);
        if (item != null) {
            item.setStock(stock);
            logger.info("更新兑换物品库存 - ID: {}, 库存: {}", itemId, stock);
            return true;
        }
        return false;
    }

    // 处理兑换申请
    public boolean processExchangeRequest(String requestId, boolean approved) {
        ExchangeRequest request = exchangeRequests.get(requestId);
        if (request != null) {
            request.setStatus(approved ? "APPROVED" : "REJECTED");
            request.setProcessTime(LocalDateTime.now());
            
            if (approved) {
                Player player = players.get(request.getPlayerId());
                ExchangeItem item = exchangeItems.get(request.getItemId());
                if (player != null && item != null && item.getStock() > 0) {
                    player.setTotalPoints(player.getTotalPoints() - item.getPoints());
                    item.setStock(item.getStock() - 1);
                    // 记录操作日志
                    logOperation("EXCHANGE_APPROVED", request.getPlayerId(), -item.getPoints(), "兑换" + item.getName());
                    logger.info("兑换成功 - 玩家: {}, 物品: {}, 积分: {}", 
                            request.getPlayerId(), item.getName(), item.getPoints());
                }
            } else {
                logger.info("兑换拒绝 - 申请: {}", requestId);
            }
            return true;
        }
        return false;
    }

    // 管理玩家
    public void addPlayer(Player player) {
        players.put(player.getId(), player);
        logger.info("添加玩家 - ID: {}, 昵称: {}", player.getId(), player.getNickname());
    }

    // 获取玩家
    public Player getPlayer(String playerId) {
        return players.get(playerId);
    }

    // 获取所有玩家
    public List<Player> getAllPlayers() {
        return new ArrayList<>(players.values());
    }

    // 获取赛事
    public Tournament getTournament(String tournamentId) {
        return tournaments.get(tournamentId);
    }

    // 获取所有赛事
    public List<Tournament> getAllTournaments() {
        return new ArrayList<>(tournaments.values());
    }

    // 获取兑换项目
    public List<ExchangeItem> getExchangeItems() {
        return new ArrayList<>(exchangeItems.values());
    }

    // 获取兑换申请
    public List<ExchangeRequest> getExchangeRequests() {
        return new ArrayList<>(exchangeRequests.values());
    }

    // 记录操作日志
    private void logOperation(String operationType, String playerId, int amount, String reason) {
        // 实际实现需要将日志存储到数据库或文件
        logger.debug("{} - Player: {} - Amount: {} - Reason: {}", operationType, playerId, amount, reason);
    }

    // 清理资源
    public void cleanup() {
        tournaments.clear();
        players.clear();
        exchangeItems.clear();
        exchangeRequests.clear();
        logger.info("清理资源完成");
    }
}

