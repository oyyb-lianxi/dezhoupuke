package com.example.depu.mapper;

import com.example.depu.model.ExchangeItem;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ExchangeItemMapper {
    // 根据ID查询兑换物品
    ExchangeItem selectById(String id);
    
    // 查询所有兑换物品
    List<ExchangeItem> selectAll();
    
    // 插入兑换物品
    void insert(ExchangeItem exchangeItem);
    
    // 更新兑换物品
    void update(ExchangeItem exchangeItem);
    
    // 删除兑换物品
    void delete(String id);
}
