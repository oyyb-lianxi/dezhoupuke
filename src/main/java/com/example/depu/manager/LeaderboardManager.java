package com.example.depu.manager;

import com.example.depu.model.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderboardManager {
    private static LeaderboardManager instance;
    private List<Player> levelLeaderboard;
    private List<Player> totalPointsLeaderboard;

    private LeaderboardManager() {
        levelLeaderboard = new ArrayList<>();
        totalPointsLeaderboard = new ArrayList<>();
    }

    public static synchronized LeaderboardManager getInstance() {
        if (instance == null) {
            instance = new LeaderboardManager();
        }
        return instance;
    }

    // 更新等级排行榜
    public void updateLevelLeaderboard(List<Player> players) {
        levelLeaderboard.clear();
        levelLeaderboard.addAll(players);
        Collections.sort(levelLeaderboard, new LevelComparator());
    }

    // 更新总积分排行榜
    public void updateTotalPointsLeaderboard(List<Player> players) {
        totalPointsLeaderboard.clear();
        totalPointsLeaderboard.addAll(players);
        Collections.sort(totalPointsLeaderboard, new TotalPointsComparator());
    }

    // 获取等级排行榜
    public List<Player> getLevelLeaderboard() {
        return levelLeaderboard;
    }

    // 获取总积分排行榜
    public List<Player> getTotalPointsLeaderboard() {
        return totalPointsLeaderboard;
    }

    // 获取玩家在等级排行榜中的排名
    public int getLevelRank(String playerId) {
        for (int i = 0; i < levelLeaderboard.size(); i++) {
            if (levelLeaderboard.get(i).getId().equals(playerId)) {
                return i + 1;
            }
        }
        return -1;
    }

    // 获取玩家在总积分排行榜中的排名
    public int getTotalPointsRank(String playerId) {
        for (int i = 0; i < totalPointsLeaderboard.size(); i++) {
            if (totalPointsLeaderboard.get(i).getId().equals(playerId)) {
                return i + 1;
            }
        }
        return -1;
    }

    // 等级排行榜比较器
    private class LevelComparator implements Comparator<Player> {
        @Override
        public int compare(Player p1, Player p2) {
            if (p1.getLevel() != p2.getLevel()) {
                return Integer.compare(p2.getLevel(), p1.getLevel());
            } else {
                return Integer.compare(p2.getLevelExperience(), p1.getLevelExperience());
            }
        }
    }

    // 总积分排行榜比较器
    private class TotalPointsComparator implements Comparator<Player> {
        @Override
        public int compare(Player p1, Player p2) {
            return Integer.compare(p2.getTotalPoints(), p1.getTotalPoints());
        }
    }

    // 清理资源
    public void cleanup() {
        levelLeaderboard.clear();
        totalPointsLeaderboard.clear();
    }
}