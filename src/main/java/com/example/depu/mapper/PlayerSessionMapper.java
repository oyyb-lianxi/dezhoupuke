package com.example.depu.mapper;

import com.example.depu.model.PlayerSession;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface PlayerSessionMapper {
    // 根据ID查询玩家会话
    PlayerSession selectById(String id);
    
    // 根据玩家ID查询玩家会话
    List<PlayerSession> selectByPlayerId(String playerId);
    
    // 查询所有玩家会话
    List<PlayerSession> selectAll();
    
    // 插入玩家会话
    void insert(PlayerSession playerSession);
    
    // 更新玩家会话
    void update(PlayerSession playerSession);
    
    // 删除玩家会话
    void delete(String id);
}
