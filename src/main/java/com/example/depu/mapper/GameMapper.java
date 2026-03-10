package com.example.depu.mapper;

import com.example.depu.model.Game;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface GameMapper {
    // 根据ID查询游戏
    Game selectById(String id);
    
    // 根据桌台ID查询游戏
    List<Game> selectByTableId(String tableId);
    
    // 查询所有游戏
    List<Game> selectAll();
    
    // 插入游戏
    void insert(Game game);
    
    // 更新游戏
    void update(Game game);
    
    // 删除游戏
    void delete(String id);
}
