package com.example.depu.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Tournament implements java.io.Serializable {
    private static final Logger logger = LoggerFactory.getLogger(Tournament.class);
    /**
     * 赛事唯一标识符
     */
    private String id;
    
    /**
     * 赛事名称
     */
    private String name;
    
    /**
     * 赛事开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 赛事结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 赛事桌台列表
     */
    private List<Table> tables;
    
    /**
     * 最大桌台数量
     */
    private int maxTables;
    
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
     * 赛事是否正在运行
     */
    private boolean isRunning;
    
    /**
     * 赛事状态
     */
    private TournamentStatus status;

    public enum TournamentStatus {
        CREATED, RUNNING, PAUSED, ENDED
    }

    public Tournament(String name, int maxTables, int smallBlind, int bigBlind, int blindLimit) {
        this.id = generateId();
        this.name = name;
        this.tables = new ArrayList<>();
        this.maxTables = maxTables;
        this.smallBlind = smallBlind;
        this.bigBlind = bigBlind;
        this.blindLimit = blindLimit;
        this.status = TournamentStatus.CREATED;
        this.isRunning = false;
        logger.info("创建赛事: ID={}, 名称={}, 最大桌台数={}, 小盲={}, 大盲={}", id, name, maxTables, smallBlind, bigBlind);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public List<Table> getTables() { return tables; }
    public void setTables(List<Table> tables) { this.tables = tables; }

    public int getMaxTables() { return maxTables; }
    public void setMaxTables(int maxTables) { this.maxTables = maxTables; }

    public int getSmallBlind() { return smallBlind; }
    public void setSmallBlind(int smallBlind) { this.smallBlind = smallBlind; }

    public int getBigBlind() { return bigBlind; }
    public void setBigBlind(int bigBlind) { this.bigBlind = bigBlind; }

    public int getBlindLimit() { return blindLimit; }
    public void setBlindLimit(int blindLimit) { this.blindLimit = blindLimit; }

    public boolean isRunning() { return isRunning; }
    public void setRunning(boolean running) { isRunning = running; }

    public TournamentStatus getStatus() { return status; }
    public void setStatus(TournamentStatus status) { this.status = status; }

    // 生成赛事ID
    private String generateId() {
        return "T" + System.currentTimeMillis();
    }

    // 添加桌台
    public boolean addTable(String tableId, int maxPlayers, int minPlayersToStart) {
        if (tables.size() < maxTables) {
            Table table = new Table(tableId, maxPlayers, minPlayersToStart, smallBlind, bigBlind, blindLimit);
            tables.add(table);
            logger.info("添加桌台: 赛事={}, 桌台={}, 最大玩家数={}", name, tableId, maxPlayers);
            return true;
        }
        logger.warn("添加桌台失败: 赛事={}, 已达到最大桌台数{}", name, maxTables);
        return false;
    }

    // 移除桌台
    public boolean removeTable(String tableId) {
        for (int i = 0; i < tables.size(); i++) {
            if (tables.get(i).getId().equals(tableId)) {
                tables.remove(i);
                logger.info("移除桌台: 赛事={}, 桌台={}", name, tableId);
                return true;
            }
        }
        logger.warn("移除桌台失败: 赛事={}, 桌台{}不存在", name, tableId);
        return false;
    }

    // 开始赛事
    public void start() {
        startTime = LocalDateTime.now();
        status = TournamentStatus.RUNNING;
        isRunning = true;
        // 启动所有桌台
        for (Table table : tables) {
            table.startGame();
        }
        logger.info("赛事开始: ID={}, 名称={}, 桌台数={}", id, name, tables.size());
    }

    // 暂停赛事
    public void pause() {
        status = TournamentStatus.PAUSED;
        isRunning = false;
        // 暂停所有桌台
        for (Table table : tables) {
            table.pause();
        }
        logger.info("赛事暂停: ID={}, 名称={}", id, name);
    }

    // 恢复赛事
    public void resume() {
        status = TournamentStatus.RUNNING;
        isRunning = true;
        // 恢复所有桌台
        for (Table table : tables) {
            table.resume();
        }
        logger.info("赛事恢复: ID={}, 名称={}", id, name);
    }

    // 结束赛事
    public void end() {
        endTime = LocalDateTime.now();
        status = TournamentStatus.ENDED;
        isRunning = false;
        // 结束所有桌台
        for (Table table : tables) {
            table.forceEnd();
        }
        logger.info("赛事结束: ID={}, 名称={}", id, name);
    }

    // 强制结束指定桌台
    public void forceEndTable(String tableId) {
        for (Table table : tables) {
            if (table.getId().equals(tableId)) {
                table.forceEnd();
                logger.info("强制结束桌台: 赛事={}, 桌台={}", name, tableId);
                break;
            }
        }
    }

    // 获取所有玩家
    public List<Player> getAllPlayers() {
        List<Player> allPlayers = new ArrayList<>();
        for (Table table : tables) {
            allPlayers.addAll(table.getPlayers());
            allPlayers.addAll(table.getSpectators());
        }
        return allPlayers;
    }

    // 检查赛事状态
    public void checkStatus() {
        if (!isRunning) return;
        
        boolean allTablesEnded = true;
        for (Table table : tables) {
            if (table.isRunning()) {
                allTablesEnded = false;
                // 检查桌台是否需要涨盲
                table.checkBlindIncrease();
                // 检查桌台是否结束
                table.isTournamentEnded();
            }
        }
        
        if (allTablesEnded) {
            end();
        }
    }

    // 获取指定桌台
    public Table getTable(String tableId) {
        for (Table table : tables) {
            if (table.getId().equals(tableId)) {
                return table;
            }
        }
        return null;
    }

    // 获取可用桌台（未满员）
    public List<Table> getAvailableTables() {
        List<Table> availableTables = new ArrayList<>();
        for (Table table : tables) {
            if (table.getPlayers().size() < table.getMaxPlayers()) {
                availableTables.add(table);
            }
        }
        return availableTables;
    }

    // 获取赛事统计信息
    public TournamentStats getStats() {
        int totalPlayers = 0;
        int activeTables = 0;
        int completedTables = 0;

        for (Table table : tables) {
            totalPlayers += table.getPlayers().size() + table.getSpectators().size();
            if (table.isRunning()) {
                activeTables++;
            } else if (table.getStatus() == Table.TableStatus.ENDED) {
                completedTables++;
            }
        }

        return new TournamentStats(totalPlayers, activeTables, completedTables, tables.size());
    }
}