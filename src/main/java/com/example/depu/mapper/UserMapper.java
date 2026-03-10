package com.example.depu.mapper;

import com.example.depu.model.User;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface UserMapper {
    // 根据ID查询用户
    User selectById(String id);
    
    // 根据身份证号查询用户
    User selectByIdCard(String idCard);
    
    // 查询所有用户
    List<User> selectAll();
    
    // 插入用户
    void insert(User user);
    
    // 更新用户
    void update(User user);
    
    // 删除用户
    void delete(String id);
}
