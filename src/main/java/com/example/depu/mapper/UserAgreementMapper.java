package com.example.depu.mapper;

import com.example.depu.model.UserAgreement;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface UserAgreementMapper {
    // 根据ID查询用户协议
    UserAgreement selectById(int id);
    
    // 根据玩家ID查询用户协议
    UserAgreement selectByPlayerId(String playerId);
    
    // 查询所有用户协议
    List<UserAgreement> selectAll();
    
    // 插入用户协议
    void insert(UserAgreement userAgreement);
    
    // 更新用户协议
    void update(UserAgreement userAgreement);
    
    // 删除用户协议
    void delete(int id);
}
