package com.example.depu.mapper;

import com.example.depu.model.Player;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface PlayerMapper {
    // 根据ID查询玩家
    Player selectById(String id);
    
    // 根据用户ID查询玩家
    Player selectByUserId(String userId);
    
    // 查询所有玩家
    List<Player> selectAll();
    
    // 插入玩家
    void insert(Player player);
    
    // 更新玩家
    void update(Player player);
    
    // 删除玩家
    void delete(String id);
}
