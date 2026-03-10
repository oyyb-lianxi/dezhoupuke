package com.example.depu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.depu.manager.*;
import com.example.depu.manager.SecurityManager;
import com.example.depu.model.*;

public class TestMain {
    private static final Logger logger = LoggerFactory.getLogger(TestMain.class);
    
    public static void main(String[] args) {
        logger.info("Testing Texas Hold'em Tournament System");
        
        // 初始化管理器
        AdminManager adminManager = AdminManager.getInstance();
        PlayerManager playerManager = PlayerManager.getInstance();
        GameManager gameManager = GameManager.getInstance();
        LeaderboardManager leaderboardManager = LeaderboardManager.getInstance();
        PersistenceManager persistenceManager = PersistenceManager.getInstance();
        SecurityManager securityManager = SecurityManager.getInstance();
        
        // 启动自动备份
        persistenceManager.scheduleAutoBackup();
        
        // 创建玩家
        logger.info("Creating players...");
        Player player1 = playerManager.registerPlayer("P1", "Player1", "avatar1.jpg", 25, "192.168.1.1", "Device1");
        Player player2 = playerManager.registerPlayer("P2", "Player2", "avatar2.jpg", 30, "192.168.1.2", "Device2");
        Player player3 = playerManager.registerPlayer("P3", "Player3", "avatar3.jpg", 20, "192.168.1.3", "Device3");
        
        // 添加玩家到管理员系统
        adminManager.addPlayer(player1);
        adminManager.addPlayer(player2);
        adminManager.addPlayer(player3);
        
        // 为玩家添加仓库积分
        adminManager.manageWarehousePoints(player1.getId(), 1000000, "初始积分");
        adminManager.manageWarehousePoints(player2.getId(), 1000000, "初始积分");
        adminManager.manageWarehousePoints(player3.getId(), 1000000, "初始积分");
        
        // 创建赛事
        logger.info("Creating tournament...");
        Tournament tournament = adminManager.createTournament("Test Tournament", 2, 1000, 2000, 64000);
        
        // 添加桌台
        tournament.addTable("Table1", 9, 2);
        
        // 玩家选择桌台
        logger.info("Players selecting table...");
        Table table1 = tournament.getTable("Table1");
        playerManager.selectTable(player1, table1, 400000);
        playerManager.selectTable(player2, table1, 400000);
        playerManager.selectTable(player3, table1, 400000);
        
        // 开始赛事
        logger.info("Starting tournament...");
        tournament.start();
        
        // 模拟游戏进行
        logger.info("Simulating game play...");
        // 这里可以添加更多游戏逻辑测试
        
        // 检查排行榜
        logger.info("Updating leaderboards...");
        leaderboardManager.updateLevelLeaderboard(adminManager.getAllPlayers());
        leaderboardManager.updateTotalPointsLeaderboard(adminManager.getAllPlayers());
        
        // 显示排行榜
        logger.info("Level Leaderboard:");
        for (Player player : leaderboardManager.getLevelLeaderboard()) {
            logger.info(player.getNickname() + " - Level: " + player.getLevel() + " - Experience: " + player.getLevelExperience());
        }
        
        logger.info("Total Points Leaderboard:");
        for (Player player : leaderboardManager.getTotalPointsLeaderboard()) {
            logger.info(player.getNickname() + " - Total Points: " + player.getTotalPoints());
        }
        
        // 保存数据
        logger.info("Saving data...");
        persistenceManager.savePlayers(adminManager.getAllPlayers());
        persistenceManager.saveTournaments(adminManager.getAllTournaments());
        
        // 清理资源
        logger.info("Cleaning up...");
        adminManager.cleanup();
        playerManager.cleanup();
        gameManager.cleanup();
        leaderboardManager.cleanup();
        persistenceManager.cleanup();
        securityManager.cleanup();
        
        logger.info("Test completed successfully!");
    }
}