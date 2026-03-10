package com.example.depu.service;

import com.example.depu.model.User;
import com.example.depu.model.Player;
import com.example.depu.util.SecurityUtil;
import com.example.depu.manager.PersistenceManager;
import com.example.depu.mapper.UserMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private Map<String, User> userMap; // 身份证号 -> 用户
    private Map<String, User> userIdMap; // 用户ID -> 用户
    private PersistenceManager persistenceManager;
    private UserMapper userMapper;
    
    public UserService(PersistenceManager persistenceManager, UserMapper userMapper) {
        this.persistenceManager = persistenceManager;
        this.userMapper = userMapper;
        this.userMap = new HashMap<>();
        this.userIdMap = new HashMap<>();
        loadUsers();
    }
    
    // 加载用户数据
    private void loadUsers() {
        List<User> users = userMapper.selectAll();
        for (User user : users) {
            userMap.put(user.getIdCard(), user);
            userIdMap.put(user.getId(), user);
        }
    }
    
    // 用户注册
    public User register(String idCard, String password, String name, int age) {
        if (userMap.containsKey(idCard)) {
            logger.warn("注册失败 - 身份证号已注册: {}", idCard);
            throw new RuntimeException("该身份证号已注册");
        }
        
        // 验证身份证号
        if (!validateIdCard(idCard)) {
            logger.warn("注册失败 - 身份证号格式不正确: {}", idCard);
            throw new RuntimeException("身份证号格式不正确");
        }
        
        // 通过身份证号验证年龄
        int idCardAge = calculateAgeFromIdCard(idCard);
        if (idCardAge < 18) {
            logger.warn("注册失败 - 未满18周岁: {}, 年龄: {}", idCard, idCardAge);
            throw new RuntimeException("未满18周岁禁止参与");
        }
        
        // 生成密码哈希
        String passwordHash = SecurityUtil.generatePasswordHash(password);
        
        // 创建用户
        User user = new User(idCard, passwordHash, name);
        
        // 保存到数据库
        userMapper.insert(user);
        
        // 更新内存缓存
        userMap.put(idCard, user);
        userIdMap.put(user.getId(), user);
        
        logger.info("注册成功 - 用户ID: {}, 身份证号: {}, 姓名: {}", user.getId(), idCard, name);
        return user;
    }
    
    // 用户登录
    public User login(String idCard, String password) {
        User user = userMapper.selectByIdCard(idCard);
        if (user == null) {
            logger.warn("登录失败 - 用户不存在: {}", idCard);
            throw new RuntimeException("用户不存在");
        }
        
        if (user.getStatus() != User.UserStatus.ACTIVE) {
            logger.warn("登录失败 - 账号状态异常: {}, 状态: {}", idCard, user.getStatus());
            throw new RuntimeException("账号状态异常");
        }
        
        // 验证密码
        String salt = SecurityUtil.extractSalt(user.getPassword());
        String hashedPassword = SecurityUtil.extractHashedPassword(user.getPassword());
        if (!SecurityUtil.verifyPassword(password, salt, hashedPassword)) {
            logger.warn("登录失败 - 密码错误: {}", idCard);
            throw new RuntimeException("密码错误");
        }
        
        // 通过身份证号验证年龄
        int idCardAge = calculateAgeFromIdCard(idCard);
        if (idCardAge < 18) {
            logger.warn("登录失败 - 未满18周岁: {}, 年龄: {}", idCard, idCardAge);
            throw new RuntimeException("未满18周岁禁止参与");
        }
        
        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.update(user);
        
        // 更新内存缓存
        userMap.put(idCard, user);
        userIdMap.put(user.getId(), user);
        
        logger.info("登录成功 - 用户ID: {}, 身份证号: {}", user.getId(), idCard);
        return user;
    }
    
    // 验证身份证号格式
    private boolean validateIdCard(String idCard) {
        // 简单的身份证号格式验证：18位，前17位为数字，第18位为数字或X
        if (idCard == null || idCard.length() != 18) {
            return false;
        }
        
        // 前17位必须是数字
        for (int i = 0; i < 17; i++) {
            if (!Character.isDigit(idCard.charAt(i))) {
                return false;
            }
        }
        
        // 第18位可以是数字或X
        char lastChar = idCard.charAt(17);
        return Character.isDigit(lastChar) || lastChar == 'X' || lastChar == 'x';
    }
    
    // 通过身份证号计算年龄
    private int calculateAgeFromIdCard(String idCard) {
        // 身份证号的第7-10位是出生年份，11-12位是出生月份，13-14位是出生日期
        int birthYear = Integer.parseInt(idCard.substring(6, 10));
        int birthMonth = Integer.parseInt(idCard.substring(10, 12));
        int birthDay = Integer.parseInt(idCard.substring(12, 14));
        
        // 获取当前日期
        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();
        int currentDay = now.getDayOfMonth();
        
        // 计算年龄
        int age = currentYear - birthYear;
        
        // 调整年龄：如果当前月份小于出生月份，或者月份相同但日期小于出生日期，年龄减1
        if (currentMonth < birthMonth || (currentMonth == birthMonth && currentDay < birthDay)) {
            age--;
        }
        
        return age;
    }
    
    // 根据ID获取用户
    public User getUserById(String userId) {
        User user = userIdMap.get(userId);
        if (user == null) {
            user = userMapper.selectById(userId);
            if (user != null) {
                userMap.put(user.getIdCard(), user);
                userIdMap.put(user.getId(), user);
            }
        }
        return user;
    }
    
    // 根据身份证号获取用户
    public User getUserByIdCard(String idCard) {
        User user = userMap.get(idCard);
        if (user == null) {
            user = userMapper.selectByIdCard(idCard);
            if (user != null) {
                userMap.put(idCard, user);
                userIdMap.put(user.getId(), user);
            }
        }
        return user;
    }
    
    // 保存用户数据
    private void saveUser(User user) {
        userMapper.update(user);
        // 更新内存缓存
        userMap.put(user.getIdCard(), user);
        userIdMap.put(user.getId(), user);
    }
    
    // 创建玩家关联
    public Player createPlayer(User user, String nickname) {
        Player player = new Player();
        player.setId("P" + System.currentTimeMillis());
        player.setNickname(nickname);
        player.setUserId(user.getId());
        player.setWarehousePoints(1000); // 初始积分
        player.setTablePoints(0);
        player.setPendingPoints(0);
        player.setLevelExperience(0);
        player.setTotalPoints(1000);
        player.setLevel(1);
        player.setOnline(true);
        player.setSpectating(false);
        player.setLastLoginTime(LocalDateTime.now());
        player.setRegistrationTime(LocalDateTime.now());
        player.setVerified(true); // 已通过身份证验证
        player.setAge(20); // 示例年龄
        
        return player;
    }
    
    // 用户修改密码
    public void changePassword(String userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);
        if (user == null) {
            logger.warn("修改密码失败 - 用户不存在: {}", userId);
            throw new RuntimeException("用户不存在");
        }
        
        // 验证旧密码
        String salt = SecurityUtil.extractSalt(user.getPassword());
        String hashedPassword = SecurityUtil.extractHashedPassword(user.getPassword());
        if (!SecurityUtil.verifyPassword(oldPassword, salt, hashedPassword)) {
            logger.warn("修改密码失败 - 旧密码错误: {}", userId);
            throw new RuntimeException("旧密码错误");
        }
        
        // 生成新密码哈希
        String newPasswordHash = SecurityUtil.generatePasswordHash(newPassword);
        user.setPassword(newPasswordHash);
        
        // 保存用户数据
        saveUser(user);
        
        logger.info("修改密码成功 - 用户ID: {}", userId);
    }
    
    // 管理员重置用户密码
    public void resetUserPassword(String userId, String newPassword) {
        User user = getUserById(userId);
        if (user == null) {
            logger.warn("重置密码失败 - 用户不存在: {}", userId);
            throw new RuntimeException("用户不存在");
        }
        
        // 生成新密码哈希
        String newPasswordHash = SecurityUtil.generatePasswordHash(newPassword);
        user.setPassword(newPasswordHash);
        
        // 保存用户数据
        saveUser(user);
        
        logger.info("重置密码成功 - 用户ID: {}", userId);
    }
}
