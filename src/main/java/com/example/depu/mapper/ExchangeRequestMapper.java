package com.example.depu.mapper;

import com.example.depu.model.ExchangeRequest;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ExchangeRequestMapper {
    // 根据ID查询兑换申请
    ExchangeRequest selectById(String id);
    
    // 根据玩家ID查询兑换申请
    List<ExchangeRequest> selectByPlayerId(String playerId);
    
    // 查询所有兑换申请
    List<ExchangeRequest> selectAll();
    
    // 插入兑换申请
    void insert(ExchangeRequest exchangeRequest);
    
    // 更新兑换申请
    void update(ExchangeRequest exchangeRequest);
    
    // 删除兑换申请
    void delete(String id);
}
