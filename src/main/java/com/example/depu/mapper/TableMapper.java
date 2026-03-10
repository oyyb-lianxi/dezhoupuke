package com.example.depu.mapper;

import com.example.depu.model.Table;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface TableMapper {
    // 根据ID查询桌台
    Table selectById(String id);
    
    // 查询所有桌台
    List<Table> selectAll();
    
    // 插入桌台
    void insert(Table table);
    
    // 更新桌台
    void update(Table table);
    
    // 删除桌台
    void delete(String id);
}
