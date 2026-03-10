package com.example.depu.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.depu.model.Player;
import com.example.depu.model.PlayerAction;
import com.example.depu.model.Table;
import com.example.depu.model.Tournament;
import com.example.depu.model.Game;
import com.example.depu.model.ExchangeItem;
import com.example.depu.model.ExchangeRequest;
import com.example.depu.manager.GameHistoryDetail;
import com.example.depu.manager.OpponentCardInfo;
import com.example.depu.manager.BettingRound;
import com.example.depu.mapper.PlayerMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {
    private static final Logger logger = LoggerFactory.getLogger(PlayerManager.class);
    private static PlayerManager instance;
    private ConcurrentHashMap<String, Player> players;
    private ConcurrentHashMap<String, List<GameHistoryDetail>> gameHistories;
    private ConcurrentHashMap<String, List<ExchangeRequest>> playerExchangeRequests;
    private PlayerMapper playerMapper;

    private PlayerManager() {
        players = new ConcurrentHashMap<>();
        gameHistories = new ConcurrentHashMap<>();
        playerExchangeRequests = new ConcurrentHashMap<>();
    }

    public static synchronized PlayerManager getInstance() {
        if (instance == null) {
            instance = new PlayerManager();
        }
        return instance;
    }
    
    // 设置PlayerMapper
    public void setPlayerMapper(PlayerMapper playerMapper) {
        this.playerMapper = playerMapper;
        loadPlayers();
    }

    // 加载玩家数据
    private void loadPlayers() {
        if (playerMapper != null) {
            List<Player> playerList = playerMapper.selectAll();
            logger.info("加载玩家数据: 共{}名玩家", playerList.size());
            for (Player player : playerList) {
                players.put(player.getId(), player);
                gameHistories.put(player.getId(), new ArrayList<GameHistoryDetail>());
                playerExchangeRequests.put(player.getId(), new ArrayList<>());
            }
        }
    }

    // 注册玩家
    public Player registerPlayer(String id, String nickname, String avatar, int age, String ipAddress, String deviceInfo) {
        logger.info("注册新玩家: ID={}, 昵称={}, 年龄={}", id, nickname, age);
        Player player = new Player();
        player.setId(id);
        player.setNickname(nickname);
        player.setAvatar(avatar);
        player.setAge(age);
        player.setIpAddress(ipAddress);
        player.setDeviceInfo(deviceInfo);
        player.setWarehousePoints(0);
        player.setTablePoints(0);
        player.setPendingPoints(0);
        player.setLevelExperience(0);
        player.setTotalPoints(0);
        player.setLevel(1);
        player.setOnline(true);
        player.setSpectating(false);
        player.setVerified(false);
        player.setLastLoginTime(LocalDateTime.now());
        player.setRegistrationTime(LocalDateTime.now());
        
        // 保存到数据库
        if (playerMapper != null) {
            playerMapper.insert(player);
            logger.info("玩家注册成功并保存到数据库: ID={}", id);
        }
        
        players.put(id, player);
        gameHistories.put(id, new ArrayList<GameHistoryDetail>());
        playerExchangeRequests.put(id, new ArrayList<>());
        
        return player;
    }

    // 玩家登录
    public Player login(String playerId, String ipAddress, String deviceInfo) {
        logger.info("玩家登录: ID={}, IP={}", playerId, ipAddress);
        Player player = players.get(playerId);
        if (player == null && playerMapper != null) {
            player = playerMapper.selectById(playerId);
            if (player != null) {
                players.put(playerId, player);
                gameHistories.put(playerId, new ArrayList<GameHistoryDetail>());
                playerExchangeRequests.put(playerId, new ArrayList<>());
            }
        }
        
        if (player != null) {
            player.setOnline(true);
            player.setLastLoginTime(LocalDateTime.now());
            player.setIpAddress(ipAddress);
            player.setDeviceInfo(deviceInfo);
            
            // 更新到数据库
            if (playerMapper != null) {
                playerMapper.update(player);
            }
            logger.info("玩家登录成功: 昵称={}", player.getNickname());
        } else {
            logger.warn("玩家登录失败: ID={}, 玩家不存在", playerId);
        }
        return player;
    }

    // 玩家登出
    public void logout(String playerId) {
        Player player = players.get(playerId);
        if (player != null) {
            player.setOnline(false);
            
            // 更新到数据库
            if (playerMapper != null) {
                playerMapper.update(player);
            }
            logger.info("玩家登出: 昵称={}", player.getNickname());
        } else {
            logger.warn("玩家登出失败: ID={}, 玩家不存在", playerId);
        }
    }

    // 玩家选择桌台
    public boolean selectTable(Player player, Table table, int buyIn) {
        logger.info("玩家选择桌台: 玩家={}, 桌台={}, 买入={}", player.getNickname(), table.getId(), buyIn);
        if (player.getWarehousePoints() >= buyIn && buyIn >= 400000 && buyIn <= 4000000) {
            boolean result = table.addPlayer(player);
            if (result) {
                logger.info("玩家选择桌台成功: 玩家={}, 桌台={}", player.getNickname(), table.getId());
                if (playerMapper != null) {
                    playerMapper.update(player);
                }
            } else {
                logger.warn("玩家选择桌台失败: 玩家={}, 桌台={}", player.getNickname(), table.getId());
            }
            return result;
        }
        logger.warn("玩家选择桌台失败: 玩家={}, 桌台={}, 买入金额无效或仓库积分不足", player.getNickname(), table.getId());
        return false;
    }

    // 玩家离座
    public boolean leaveTable(Player player, Table table) {
        logger.info("玩家离座: 玩家={}, 桌台={}", player.getNickname(), table.getId());
        boolean result = table.removePlayer(player.getId());
        if (result) {
            logger.info("玩家离座成功: 玩家={}, 桌台={}", player.getNickname(), table.getId());
            if (playerMapper != null) {
                playerMapper.update(player);
            }
        } else {
            logger.warn("玩家离座失败: 玩家={}, 桌台={}", player.getNickname(), table.getId());
        }
        return result;
    }

    // 玩家补分
    public boolean addPoints(Player player, int points) {
        logger.info("玩家补分: 玩家={}, 补分数额={}", player.getNickname(), points);
        if (player.getWarehousePoints() >= points && points >= 400000 && points <= 4000000) {
            boolean result = player.addPendingPoints(points);
            if (result) {
                logger.info("玩家补分成功: 玩家={}, 补分数额={}", player.getNickname(), points);
                if (playerMapper != null) {
                    playerMapper.update(player);
                }
            } else {
                logger.warn("玩家补分失败: 玩家={}, 补分数额={}", player.getNickname(), points);
            }
            return result;
        }
        logger.warn("玩家补分失败: 玩家={}, 补分数额无效或仓库积分不足", player.getNickname());
        return false;
    }

    // 玩家重新加入
    public boolean rejoin(Player player, Table table, int buyIn) {
        logger.info("玩家重新加入: 玩家={}, 桌台={}, 买入={}", player.getNickname(), table.getId(), buyIn);
        if (player.getWarehousePoints() >= buyIn && buyIn >= 400000 && buyIn <= 4000000) {
            boolean result = table.moveFromSpectators(player, buyIn);
            if (result) {
                logger.info("玩家重新加入成功: 玩家={}, 桌台={}", player.getNickname(), table.getId());
                if (playerMapper != null) {
                    playerMapper.update(player);
                }
            } else {
                logger.warn("玩家重新加入失败: 玩家={}, 桌台={}", player.getNickname(), table.getId());
            }
            return result;
        }
        logger.warn("玩家重新加入失败: 玩家={}, 桌台={}, 买入金额无效或仓库积分不足", player.getNickname(), table.getId());
        return false;
    }

    // 玩家下注
    public void placeBet(Game game, Player player, PlayerAction.ActionType actionType, int amount) {
        logger.info("玩家下注: 玩家={}, 操作={}, 金额={}", player.getNickname(), actionType, amount);
        GameManager.getInstance().handlePlayerAction(game, player, actionType, amount);
        
        // 更新玩家积分
        if (playerMapper != null) {
            playerMapper.update(player);
        }
    }

    // 获取玩家游戏历史
    public List<GameHistoryDetail> getGameHistory(String playerId) {
        return gameHistories.getOrDefault(playerId, new ArrayList<>());
    }

    // 添加游戏历史
    public void addGameHistory(String playerId, GameHistoryDetail history) {
        List<GameHistoryDetail> histories = gameHistories.get(playerId);
        if (histories != null) {
            histories.add(history);
            logger.info("添加游戏历史: 玩家ID={}, 游戏结果={}", playerId, history.getPlayerResult());
        }
    }

    // 提交兑换申请
    public ExchangeRequest submitExchangeRequest(String playerId, String itemId) {
        logger.info("提交兑换申请: 玩家ID={}, 物品ID={}", playerId, itemId);
        Player player = getPlayer(playerId);
        if (player != null) {
            AdminManager adminManager = AdminManager.getInstance();
            ExchangeItem item = adminManager.getExchangeItems().stream()
                    .filter(i -> i.getId().equals(itemId))
                    .findFirst()
                    .orElse(null);
            
            if (item != null && item.getStock() > 0 && player.getTotalPoints() >= item.getPoints()) {
                ExchangeRequest request = new ExchangeRequest("ER" + System.currentTimeMillis(), playerId, itemId);
                List<ExchangeRequest> requests = playerExchangeRequests.get(playerId);
                if (requests != null) {
                    requests.add(request);
                }
                adminManager.getExchangeRequests().add(request);
                logger.info("兑换申请提交成功: 玩家={}, 物品={}", player.getNickname(), item.getName());
                return request;
            } else {
                logger.warn("兑换申请提交失败: 玩家={}, 物品不存在或库存不足或积分不足", player.getNickname());
            }
        } else {
            logger.warn("兑换申请提交失败: 玩家ID={}, 玩家不存在", playerId);
        }
        return null;
    }

    // 获取玩家兑换申请
    public List<ExchangeRequest> getExchangeRequests(String playerId) {
        return playerExchangeRequests.getOrDefault(playerId, new ArrayList<>());
    }

    // 获取玩家
    public Player getPlayer(String playerId) {
        Player player = players.get(playerId);
        if (player == null && playerMapper != null) {
            player = playerMapper.selectById(playerId);
            if (player != null) {
                players.put(playerId, player);
                gameHistories.put(playerId, new ArrayList<>());
                playerExchangeRequests.put(playerId, new ArrayList<>());
            }
        }
        return player;
    }

    // 获取所有玩家
    public List<Player> getAllPlayers() {
        if (playerMapper != null) {
            return playerMapper.selectAll();
        }
        return new ArrayList<>(players.values());
    }
    
    // 根据userId获取玩家
    public Player getPlayerByUserId(String userId) {
        Player player = null;
        for (Player p : players.values()) {
            if (userId.equals(p.getUserId())) {
                player = p;
                break;
            }
        }
        
        if (player == null && playerMapper != null) {
            player = playerMapper.selectByUserId(userId);
            if (player != null) {
                players.put(player.getId(), player);
                gameHistories.put(player.getId(), new ArrayList<GameHistoryDetail>());
                playerExchangeRequests.put(player.getId(), new ArrayList<>());
            }
        }
        return player;
    }
    
    // 添加玩家
    public void addPlayer(Player player) {
        // 保存到数据库
        if (playerMapper != null) {
            playerMapper.insert(player);
        }
        
        players.put(player.getId(), player);
        gameHistories.put(player.getId(), new ArrayList<GameHistoryDetail>());
        playerExchangeRequests.put(player.getId(), new ArrayList<>());
        logger.info("添加玩家: ID={}, 昵称={}", player.getId(), player.getNickname());
    }

    // 获取可用桌台
    public List<Table> getAvailableTables(Tournament tournament) {
        List<Table> availableTables = tournament.getAvailableTables();
        logger.info("获取可用桌台: 赛事={}, 可用桌台数={}", tournament.getName(), availableTables.size());
        return availableTables;
    }

    // 检查玩家年龄
    public boolean checkAgeRestriction(int age) {
        boolean result = age >= 18;
        logger.info("检查年龄限制: 年龄={}, 结果={}", age, result);
        return result;
    }

    // 检查玩家是否在其他桌台
    public boolean isPlayerAtOtherTable(Player player, Table currentTable) {
        AdminManager adminManager = AdminManager.getInstance();
        for (Tournament tournament : adminManager.getAllTournaments()) {
            for (Table table : tournament.getTables()) {
                if (!table.getId().equals(currentTable.getId())) {
                    for (Player p : table.getPlayers()) {
                        if (p.getId().equals(player.getId())) {
                            logger.info("玩家在其他桌台: 玩家={}, 桌台={}", player.getNickname(), table.getId());
                            return true;
                        }
                    }
                    for (Player p : table.getSpectators()) {
                        if (p.getId().equals(player.getId())) {
                            logger.info("玩家在其他桌台旁观: 玩家={}, 桌台={}", player.getNickname(), table.getId());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // 清理资源
    public void cleanup() {
        logger.info("清理玩家管理器资源");
        players.clear();
        gameHistories.clear();
        playerExchangeRequests.clear();
    }
}