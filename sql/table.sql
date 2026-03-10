-- 创建桌台表
CREATE TABLE IF NOT EXISTS `table` (
  `id` VARCHAR(50) PRIMARY KEY COMMENT '桌台唯一标识符',
  `max_players` INT NOT NULL COMMENT '桌台最大玩家数量',
  `min_players_to_start` INT NOT NULL COMMENT '自动开启游戏的最小玩家数',
  `small_blind` INT NOT NULL COMMENT '小盲注金额',
  `big_blind` INT NOT NULL COMMENT '大盲注金额',
  `blind_limit` INT NOT NULL COMMENT '盲注上限',
  `blind_increase_interval` INT NOT NULL DEFAULT 10 COMMENT '涨盲周期（分钟）',
  `last_blind_increase_time` DATETIME COMMENT '上次涨盲的时间',
  `start_time` DATETIME COMMENT '桌台开始时间',
  `tournament_duration` INT NOT NULL DEFAULT 60 COMMENT '单场比赛时长（分钟）',
  `is_running` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '桌台是否正在运行',
  `is_paused` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '桌台是否暂停',
  `pot` INT NOT NULL DEFAULT 0 COMMENT '当前底池金额',
  `current_bet` INT NOT NULL DEFAULT 0 COMMENT '当前下注金额',
  `status` ENUM('WAITING', 'RUNNING', 'PAUSED', 'ENDED') NOT NULL DEFAULT 'WAITING' COMMENT '桌台状态',
  `current_game_id` VARCHAR(50) COMMENT '当前正在进行的游戏ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='桌台表';

-- 创建桌台玩家关联表
CREATE TABLE IF NOT EXISTS `table_player` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
  `table_id` VARCHAR(50) NOT NULL COMMENT '桌台ID',
  `player_id` VARCHAR(50) NOT NULL COMMENT '玩家ID',
  `join_time` DATETIME NOT NULL DEFAULT NOW() COMMENT '加入时间',
  FOREIGN KEY (`table_id`) REFERENCES `table` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`player_id`) REFERENCES `player` (`id`) ON DELETE CASCADE,
  UNIQUE KEY `uk_table_player` (`table_id`, `player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='桌台玩家关联表';

-- 创建桌台观战玩家关联表
CREATE TABLE IF NOT EXISTS `table_spectator` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
  `table_id` VARCHAR(50) NOT NULL COMMENT '桌台ID',
  `player_id` VARCHAR(50) NOT NULL COMMENT '玩家ID',
  `join_time` DATETIME NOT NULL DEFAULT NOW() COMMENT '加入时间',
  FOREIGN KEY (`table_id`) REFERENCES `table` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`player_id`) REFERENCES `player` (`id`) ON DELETE CASCADE,
  UNIQUE KEY `uk_table_spectator` (`table_id`, `player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='桌台观战玩家关联表';

-- 插入测试数据
INSERT INTO `table` (`id`, `max_players`, `min_players_to_start`, `small_blind`, `big_blind`, `blind_limit`, `blind_increase_interval`, `last_blind_increase_time`, `start_time`, `tournament_duration`, `is_running`, `is_paused`, `pot`, `current_bet`, `status`, `current_game_id`) VALUES
('TBL001', 9, 2, 10, 20, 1000, 10, NOW(), NOW(), 60, TRUE, FALSE, 500, 100, 'RUNNING', NULL),
('TBL002', 6, 2, 5, 10, 500, 10, NULL, NULL, 60, FALSE, FALSE, 0, 0, 'WAITING', NULL),
('TBL003', 9, 2, 20, 40, 2000, 10, NOW(), NOW(), 60, TRUE, FALSE, 1000, 200, 'RUNNING', NULL),
('TBL004', 6, 2, 10, 20, 1000, 10, NULL, NULL, 60, FALSE, FALSE, 0, 0, 'WAITING', NULL),
('TBL005', 9, 2, 5, 10, 500, 10, NULL, NULL, 60, FALSE, FALSE, 0, 0, 'WAITING', NULL);

-- 插入桌台玩家关联数据
INSERT INTO `table_player` (`table_id`, `player_id`, `join_time`) VALUES
('TBL001', 'P1772807029000', NOW()),
('TBL001', 'P1772807029001', NOW()),
('TBL001', 'P1772807029002', NOW()),
('TBL003', 'P1772807029003', NOW()),
('TBL003', 'P1772807029004', NOW());

-- 插入桌台观战玩家关联数据
INSERT INTO `table_spectator` (`table_id`, `player_id`, `join_time`) VALUES
('TBL001', 'P1772807029003', NOW()),
('TBL003', 'P1772807029000', NOW());
