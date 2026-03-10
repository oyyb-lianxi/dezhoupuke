package com.example.depu.config;

import com.example.depu.manager.PersistenceManager;
import com.example.depu.service.UserService;
import com.example.depu.manager.PlayerManager;
import com.example.depu.mapper.UserMapper;
import com.example.depu.mapper.PlayerMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public PersistenceManager persistenceManager() {
        return PersistenceManager.getInstance();
    }
    
    @Bean
    public UserService userService(PersistenceManager persistenceManager, UserMapper userMapper) {
        return new UserService(persistenceManager, userMapper);
    }
    
    @Bean
    public PlayerManager playerManager(PlayerMapper playerMapper) {
        PlayerManager playerManager = PlayerManager.getInstance();
        playerManager.setPlayerMapper(playerMapper);
        return playerManager;
    }
}
