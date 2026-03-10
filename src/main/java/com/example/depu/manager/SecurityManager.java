package com.example.depu.manager;

import com.example.depu.model.Player;
import com.example.depu.model.Table;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SecurityManager {
    private static SecurityManager instance;
    private ConcurrentHashMap<String, PlayerSession> playerSessions;
    private ConcurrentHashMap<String, TableIPMap> tableIPMaps;
    private ConcurrentHashMap<String, UserAgreement> userAgreements;

    private SecurityManager() {
        playerSessions = new ConcurrentHashMap<>();
        tableIPMaps = new ConcurrentHashMap<>();
        userAgreements = new ConcurrentHashMap<>();
    }

    public static synchronized SecurityManager getInstance() {
        if (instance == null) {
            instance = new SecurityManager();
        }
        return instance;
    }

    // 验证玩家年龄
    public boolean verifyAge(int age) {
        return age >= 18;
    }

    // 验证玩家身份
    public boolean verifyIdentity(String playerId, String identityInfo) {
        // 实际实现需要对接网吧实名认证系统
        return true;
    }

    // 检查IP限制（防伙牌）
    public boolean checkIPRestriction(Table table, String ipAddress) {
        TableIPMap ipMap = tableIPMaps.get(table.getId());
        if (ipMap == null) {
            ipMap = new TableIPMap();
            tableIPMaps.put(table.getId(), ipMap);
        }
        
        int count = ipMap.getIPCount(ipAddress);
        return count < 2; // 同一IP最多2人
    }

    // 记录IP使用
    public void recordIPUsage(Table table, String ipAddress, String playerId) {
        TableIPMap ipMap = tableIPMaps.get(table.getId());
        if (ipMap == null) {
            ipMap = new TableIPMap();
            tableIPMaps.put(table.getId(), ipMap);
        }
        ipMap.addIPUsage(ipAddress, playerId);
    }

    // 移除IP使用记录
    public void removeIPUsage(Table table, String playerId) {
        TableIPMap ipMap = tableIPMaps.get(table.getId());
        if (ipMap != null) {
            ipMap.removeIPUsage(playerId);
        }
    }

    // 检查用户协议签署
    public boolean checkUserAgreement(String playerId) {
        UserAgreement agreement = userAgreements.get(playerId);
        return agreement != null && agreement.isAccepted();
    }

    // 签署用户协议
    public void acceptUserAgreement(String playerId) {
        UserAgreement agreement = new UserAgreement(playerId, true);
        userAgreements.put(playerId, agreement);
    }

    // 创建玩家会话
    public String createSession(String playerId, String ipAddress, String deviceInfo) {
        String sessionId = generateSessionId();
        PlayerSession session = new PlayerSession(sessionId, playerId, ipAddress, deviceInfo);
        playerSessions.put(sessionId, session);
        return sessionId;
    }

    // 验证会话
    public boolean validateSession(String sessionId) {
        PlayerSession session = playerSessions.get(sessionId);
        return session != null && !session.isExpired();
    }

    // 结束会话
    public void endSession(String sessionId) {
        playerSessions.remove(sessionId);
    }

    // 检查多客户端登录
    public boolean checkMultipleLogin(String playerId, String sessionId) {
        for (PlayerSession session : playerSessions.values()) {
            if (session.getPlayerId().equals(playerId) && !session.getSessionId().equals(sessionId)) {
                return true;
            }
        }
        return false;
    }

    // 强制下线其他会话
    public void forceLogoutOtherSessions(String playerId, String currentSessionId) {
        for (Map.Entry<String, PlayerSession> entry : playerSessions.entrySet()) {
            PlayerSession session = entry.getValue();
            if (session.getPlayerId().equals(playerId) && !session.getSessionId().equals(currentSessionId)) {
                playerSessions.remove(entry.getKey());
            }
        }
    }

    // 检测异常行为
    public boolean detectAbnormalBehavior(Player player, Table table) {
        // 实际实现需要更复杂的逻辑
        // 例如：异常胜率、异常下注模式等
        return false;
    }

    // 生成会话ID
    private String generateSessionId() {
        return "S" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000);
    }

    // 清理资源
    public void cleanup() {
        playerSessions.clear();
        tableIPMaps.clear();
        userAgreements.clear();
    }
}