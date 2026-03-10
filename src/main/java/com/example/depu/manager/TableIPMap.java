package com.example.depu.manager;

import java.util.HashMap;
import java.util.Map;

public class TableIPMap {
    /**
     * IP地址到玩家ID的映射，用于防止同一IP多开
     * 结构：IP地址 -> (玩家ID -> 玩家ID)
     */
    private Map<String, Map<String, String>> ipToPlayers;

    public TableIPMap() {
        ipToPlayers = new HashMap<>();
    }

    public void addIPUsage(String ipAddress, String playerId) {
        if (!ipToPlayers.containsKey(ipAddress)) {
            ipToPlayers.put(ipAddress, new HashMap<>());
        }
        ipToPlayers.get(ipAddress).put(playerId, playerId);
    }

    public void removeIPUsage(String playerId) {
        for (Map<String, String> players : ipToPlayers.values()) {
            players.remove(playerId);
        }
    }

    public int getIPCount(String ipAddress) {
        Map<String, String> players = ipToPlayers.get(ipAddress);
        return players != null ? players.size() : 0;
    }
}
