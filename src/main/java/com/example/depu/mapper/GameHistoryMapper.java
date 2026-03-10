package com.example.depu.mapper;

import com.example.depu.model.GameHistory;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface GameHistoryMapper {
    // 根据ID查询游戏历史
    GameHistory selectById(String id);
    
    // 根据桌台ID查询游戏历史
    List<GameHistory> selectByTableId(String tableId);
    
    // 根据玩家ID查询游戏历史
    List<GameHistory> selectByWinnerId(String winnerId);
    
    // 查询所有游戏历史
    List<GameHistory> selectAll();
    
    // 插入游戏历史
    void insert(GameHistory gameHistory);
    
    // 更新游戏历史
    void update(GameHistory gameHistory);
    
    // 删除游戏历史
    void delete(String id);
}
