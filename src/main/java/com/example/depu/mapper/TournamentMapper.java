package com.example.depu.mapper;

import com.example.depu.model.Tournament;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface TournamentMapper {
    // 根据ID查询赛事
    Tournament selectById(String id);
    
    // 查询所有赛事
    List<Tournament> selectAll();
    
    // 插入赛事
    void insert(Tournament tournament);
    
    // 更新赛事
    void update(Tournament tournament);
    
    // 删除赛事
    void delete(String id);
}
