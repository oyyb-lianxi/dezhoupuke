-- 德州扑克积分赛系统数据库表创建脚本
-- 按照依赖关系顺序创建表

-- 1. 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` VARCHAR(50) PRIMARY KEY COMMENT '用户唯一标识符',
  `id_card` VARCHAR(18) UNIQUE NOT NULL COMMENT '身份证号（登录账号）',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
  `name` VARCHAR(50) NOT NULL COMMENT '用户名',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `last_login_time` DATETIME COMMENT '最后登录时间',
  `status` ENUM('ACTIVE', 'INACTIVE', 'BANNED') NOT NULL DEFAULT 'ACTIVE' COMMENT '状态'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 创建玩家表
CREATE TABLE IF NOT EXISTS `player` (
  `id` VARCHAR(50) PRIMARY KEY COMMENT '玩家唯一ID',
  `nickname` VARCHAR(50) NOT NULL COMMENT '玩家昵称',
  `avatar` VARCHAR(255) COMMENT '玩家头像',
  `warehouse_points` INT NOT NULL DEFAULT 0 COMMENT '仓库积分',
  `table_points` INT NOT NULL DEFAULT 0 COMMENT '桌面积分',
  `pending_points` INT NOT NULL DEFAULT 0 COMMENT '待生效补分',
  `level_experience` INT NOT NULL DEFAULT 0 COMMENT '等级经验值',
  `total_points` INT NOT NULL DEFAULT 0 COMMENT '总积分',
  `level` INT NOT NULL DEFAULT 1 COMMENT '玩家等级',
  `is_online` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '在线状态',
  `is_spectating` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '观战状态',
  `last_login_time` DATETIME COMMENT '最后登录时间',
  `registration_time` DATETIME NOT NULL COMMENT '注册时间',
  `is_verified` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '实名验证状态',
  `age` INT COMMENT '年龄',
  `ip_address` VARCHAR(50) COMMENT 'IP地址',
  `device_info` VARCHAR(255) COMMENT '设备信息',
  `user_id` VARCHAR(50) COMMENT '关联的用户ID',
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家表';

-- 3. 创建卡牌表
CREATE TABLE IF NOT EXISTS `card` (
  `id` VARCHAR(50) PRIMARY KEY COMMENT '卡牌唯一标识符',
  `suit` VARCHAR(20) NOT NULL COMMENT '花色（如Hearts、Diamonds、Clubs、Spades）',
  `rank` VARCHAR(10) NOT NULL COMMENT '点数（如A、2-10、J、Q、K）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='卡牌表';

-- 4. 创建桌台表
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

-- 5. 创建游戏表
CREATE TABLE IF NOT EXISTS `game` (
  `id` VARCHAR(50) PRIMARY KEY COMMENT '游戏唯一标识符',
  `table_id` VARCHAR(50) NOT NULL COMMENT '游戏所属的桌台ID',
  `pot` INT NOT NULL DEFAULT 0 COMMENT '底池金额',
  `current_bet` INT NOT NULL DEFAULT 0 COMMENT '当前下注金额',
  `dealer_position` INT NOT NULL COMMENT '庄家位置',
  `current_player_index` INT NOT NULL COMMENT '当前玩家索引',
  `round` INT NOT NULL DEFAULT 0 COMMENT '游戏轮次',
  `winner_id` VARCHAR(50) COMMENT '获胜者ID',
  `is_complete` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '游戏是否完成',
  `state` ENUM('PREFLOP', 'FLOP', 'TURN', 'RIVER', 'SHOWDOWN', 'COMPLETED') NOT NULL DEFAULT 'PREFLOP' COMMENT '游戏状态',
  FOREIGN KEY (`table_id`) REFERENCES `table` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`winner_id`) REFERENCES `player` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='游戏表';

-- 6. 创建游戏玩家关联表
CREATE TABLE IF NOT EXISTS `game_player` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
  `game_id` VARCHAR(50) NOT NULL COMMENT '游戏ID',
  `player_id` VARCHAR(50) NOT NULL COMMENT '玩家ID',
  FOREIGN KEY (`game_id`) REFERENCES `game` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`player_id`) REFERENCES `player` (`id`) ON DELETE CASCADE,
  UNIQUE KEY `uk_game_player` (`game_id`, `player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='游戏玩家关联表';

-- 7. 创建游戏公共牌关联表
CREATE TABLE IF NOT EXISTS `game_community_card` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
  `game_id` VARCHAR(50) NOT NULL COMMENT '游戏ID',
  `card_id` VARCHAR(50) NOT NULL COMMENT '卡牌ID',
  `position` INT NOT NULL COMMENT '位置',
  FOREIGN KEY (`game_id`) REFERENCES `game` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`card_id`) REFERENCES `card` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='游戏公共牌关联表';

-- 8. 创建游戏动作表
CREATE TABLE IF NOT EXISTS `game_action` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '动作ID',
  `game_id` VARCHAR(50) NOT NULL COMMENT '游戏ID',
  `player_id` VARCHAR(50) NOT NULL COMMENT '玩家ID',
  `action_type` ENUM('FOLD', 'CHECK', 'CALL', 'BET', 'RAISE', 'ALL_IN', 'SMALL_BLIND', 'BIG_BLIND') NOT NULL COMMENT '动作类型',
  `amount` INT NOT NULL DEFAULT 0 COMMENT '动作金额',
  `action_time` DATETIME NOT NULL DEFAULT NOW() COMMENT '动作时间',
  FOREIGN KEY (`game_id`) REFERENCES `game` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`player_id`) REFERENCES `player` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='游戏动作表';

-- 9. 创建赛事表
CREATE TABLE IF NOT EXISTS `tournament` (
  `id` VARCHAR(50) PRIMARY KEY COMMENT '赛事唯一标识符',
  `name` VARCHAR(100) NOT NULL COMMENT '赛事名称',
  `start_time` DATETIME COMMENT '赛事开始时间',
  `end_time` DATETIME COMMENT '赛事结束时间',
  `max_tables` INT NOT NULL COMMENT '最大桌台数量',
  `small_blind` INT NOT NULL COMMENT '小盲注金额',
  `big_blind` INT NOT NULL COMMENT '大盲注金额',
  `blind_limit` INT NOT NULL COMMENT '盲注上限',
  `is_running` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '赛事是否正在运行',
  `status` ENUM('CREATED', 'RUNNING', 'PAUSED', 'ENDED') NOT NULL DEFAULT 'CREATED' COMMENT '赛事状态'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛事表';

-- 10. 创建赛事桌台关联表
CREATE TABLE IF NOT EXISTS `tournament_table` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
  `tournament_id` VARCHAR(50) NOT NULL COMMENT '赛事ID',
  `table_id` VARCHAR(50) NOT NULL COMMENT '桌台ID',
  `add_time` DATETIME NOT NULL DEFAULT NOW() COMMENT '添加时间',
  FOREIGN KEY (`tournament_id`) REFERENCES `tournament` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`table_id`) REFERENCES `table` (`id`) ON DELETE CASCADE,
  UNIQUE KEY `uk_tournament_table` (`tournament_id`, `table_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛事桌台关联表';

-- 11. 创建桌台玩家关联表
CREATE TABLE IF NOT EXISTS `table_player` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
  `table_id` VARCHAR(50) NOT NULL COMMENT '桌台ID',
  `player_id` VARCHAR(50) NOT NULL COMMENT '玩家ID',
  `join_time` DATETIME NOT NULL DEFAULT NOW() COMMENT '加入时间',
  FOREIGN KEY (`table_id`) REFERENCES `table` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`player_id`) REFERENCES `player` (`id`) ON DELETE CASCADE,
  UNIQUE KEY `uk_table_player` (`table_id`, `player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='桌台玩家关联表';

-- 12. 创建桌台观战玩家关联表
CREATE TABLE IF NOT EXISTS `table_spectator` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
  `table_id` VARCHAR(50) NOT NULL COMMENT '桌台ID',
  `player_id` VARCHAR(50) NOT NULL COMMENT '玩家ID',
  `join_time` DATETIME NOT NULL DEFAULT NOW() COMMENT '加入时间',
  FOREIGN KEY (`table_id`) REFERENCES `table` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`player_id`) REFERENCES `player` (`id`) ON DELETE CASCADE,
  UNIQUE KEY `uk_table_spectator` (`table_id`, `player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='桌台观战玩家关联表';

-- 13. 创建兑换物品表
CREATE TABLE IF NOT EXISTS `exchange_item` (
  `id` VARCHAR(50) PRIMARY KEY COMMENT '兑换物品唯一标识符',
  `name` VARCHAR(100) NOT NULL COMMENT '兑换物品名称',
  `points` INT NOT NULL COMMENT '兑换所需积分',
  `stock` INT NOT NULL COMMENT '库存数量',
  `type` VARCHAR(50) NOT NULL COMMENT '物品类型'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='兑换物品表';

-- 14. 创建兑换申请表
CREATE TABLE IF NOT EXISTS `exchange_request` (
  `id` VARCHAR(50) PRIMARY KEY COMMENT '兑换请求唯一标识符',
  `player_id` VARCHAR(50) NOT NULL COMMENT '玩家唯一标识符',
  `item_id` VARCHAR(50) NOT NULL COMMENT '兑换物品唯一标识符',
  `request_time` DATETIME NOT NULL COMMENT '请求时间',
  `process_time` DATETIME COMMENT '处理时间',
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '请求状态（PENDING、APPROVED、REJECTED）',
  FOREIGN KEY (`player_id`) REFERENCES `player` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`item_id`) REFERENCES `exchange_item` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='兑换申请表';

-- 15. 创建游戏历史表
CREATE TABLE IF NOT EXISTS `game_history` (
  `id` VARCHAR(50) PRIMARY KEY COMMENT '游戏历史记录唯一标识符',
  `table_id` VARCHAR(50) NOT NULL COMMENT '桌台唯一标识符',
  `game_id` VARCHAR(50) NOT NULL COMMENT '游戏唯一标识符',
  `start_time` DATETIME NOT NULL COMMENT '游戏开始时间',
  `end_time` DATETIME COMMENT '游戏结束时间',
  `pot` INT NOT NULL DEFAULT 0 COMMENT '底池金额',
  `winner_id` VARCHAR(50) COMMENT '获胜者唯一标识符',
  `player_result` INT NOT NULL DEFAULT 0 COMMENT '玩家结果（正为赢，负为输）',
  FOREIGN KEY (`table_id`) REFERENCES `table` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`game_id`) REFERENCES `game` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`winner_id`) REFERENCES `player` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='游戏历史表';

-- 16. 创建玩家会话表
CREATE TABLE IF NOT EXISTS `player_session` (
  `id` VARCHAR(50) PRIMARY KEY COMMENT '会话唯一标识符',
  `player_id` VARCHAR(50) NOT NULL COMMENT '玩家唯一标识符',
  `ip_address` VARCHAR(50) NOT NULL COMMENT 'IP地址',
  `device_info` VARCHAR(255) COMMENT '设备信息',
  `create_time` BIGINT NOT NULL COMMENT '创建时间（毫秒）',
  `last_access_time` BIGINT NOT NULL COMMENT '最后访问时间（毫秒）',
  FOREIGN KEY (`player_id`) REFERENCES `player` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家会话表';

-- 17. 创建用户协议表
CREATE TABLE IF NOT EXISTS `user_agreement` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '协议ID',
  `player_id` VARCHAR(50) NOT NULL COMMENT '玩家唯一标识符',
  `accepted` BOOLEAN NOT NULL COMMENT '是否接受用户协议',
  `accept_time` BIGINT NOT NULL COMMENT '接受时间（毫秒）',
  FOREIGN KEY (`player_id`) REFERENCES `player` (`id`) ON DELETE CASCADE,
  UNIQUE KEY `uk_player_agreement` (`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户协议表';

-- 插入测试数据

-- 插入用户测试数据
INSERT INTO `user` (`id`, `id_card`, `password`, `name`, `create_time`, `last_login_time`, `status`) VALUES
('U1772807029000', '110101199001011234', 'SALT:ENCRYPTED_PASSWORD', '张三', NOW(), NOW(), 'ACTIVE'),
('U1772807029001', '110101199001011235', 'SALT:ENCRYPTED_PASSWORD', '李四', NOW(), NOW(), 'ACTIVE'),
('U1772807029002', '110101199001011236', 'SALT:ENCRYPTED_PASSWORD', '王五', NOW(), NOW(), 'ACTIVE'),
('U1772807029003', '110101199001011237', 'SALT:ENCRYPTED_PASSWORD', '赵六', NOW(), NOW(), 'ACTIVE'),
('U1772807029004', '110101199001011238', 'SALT:ENCRYPTED_PASSWORD', '钱七', NOW(), NOW(), 'ACTIVE');

-- 插入玩家测试数据
INSERT INTO `player` (`id`, `nickname`, `avatar`, `warehouse_points`, `table_points`, `pending_points`, `level_experience`, `total_points`, `level`, `is_online`, `is_spectating`, `last_login_time`, `registration_time`, `is_verified`, `age`, `ip_address`, `device_info`, `user_id`) VALUES
('P1772807029000', '德州高手', 'avatar1.png', 10000, 5000, 0, 100, 15000, 5, TRUE, FALSE, NOW(), NOW(), TRUE, 25, '192.168.1.100', 'Windows 10, Chrome', 'U1772807029000'),
('P1772807029001', '扑克大师', 'avatar2.png', 8000, 3000, 0, 80, 11000, 4, TRUE, FALSE, NOW(), NOW(), TRUE, 30, '192.168.1.101', 'MacOS, Safari', 'U1772807029001'),
('P1772807029002', '赌神', 'avatar3.png', 12000, 0, 0, 120, 12000, 5, FALSE, FALSE, NOW(), NOW(), TRUE, 28, '192.168.1.102', 'Linux, Firefox', 'U1772807029002'),
('P1772807029003', '新手小白', 'avatar4.png', 5000, 2000, 0, 20, 7000, 2, TRUE, FALSE, NOW(), NOW(), TRUE, 20, '192.168.1.103', 'Android, Chrome', 'U1772807029003'),
('P1772807029004', '老油条', 'avatar5.png', 15000, 8000, 0, 150, 23000, 6, FALSE, FALSE, NOW(), NOW(), TRUE, 35, '192.168.1.104', 'iOS, Safari', 'U1772807029004');

-- 插入卡牌测试数据
INSERT INTO `card` (`id`, `suit`, `rank`) VALUES
('C001', 'Hearts', 'A'),
('C002', 'Diamonds', 'K'),
('C003', 'Clubs', 'Q'),
('C004', 'Spades', 'J'),
('C005', 'Hearts', '10'),
('C006', 'Diamonds', '9'),
('C007', 'Clubs', '8'),
('C008', 'Spades', '7'),
('C009', 'Hearts', '6'),
('C010', 'Diamonds', '5');

-- 插入桌台测试数据
INSERT INTO `table` (`id`, `max_players`, `min_players_to_start`, `small_blind`, `big_blind`, `blind_limit`, `blind_increase_interval`, `last_blind_increase_time`, `start_time`, `tournament_duration`, `is_running`, `is_paused`, `pot`, `current_bet`, `status`, `current_game_id`) VALUES
('TBL001', 9, 2, 10, 20, 1000, 10, NOW(), NOW(), 60, TRUE, FALSE, 500, 100, 'RUNNING', NULL),
('TBL002', 6, 2, 5, 10, 500, 10, NULL, NULL, 60, FALSE, FALSE, 0, 0, 'WAITING', NULL),
('TBL003', 9, 2, 20, 40, 2000, 10, NOW(), NOW(), 60, TRUE, FALSE, 1000, 200, 'RUNNING', NULL),
('TBL004', 6, 2, 10, 20, 1000, 10, NULL, NULL, 60, FALSE, FALSE, 0, 0, 'WAITING', NULL),
('TBL005', 9, 2, 5, 10, 500, 10, NULL, NULL, 60, FALSE, FALSE, 0, 0, 'WAITING', NULL);

-- 插入游戏测试数据
INSERT INTO `game` (`id`, `table_id`, `pot`, `current_bet`, `dealer_position`, `current_player_index`, `round`, `winner_id`, `is_complete`, `state`) VALUES
('G1772807029000', 'TBL001', 500, 100, 0, 2, 1, 'P1772807029000', FALSE, 'FLOP'),
('G1772807029001', 'TBL003', 1000, 200, 1, 0, 2, NULL, FALSE, 'TURN');

-- 插入游戏玩家关联数据
INSERT INTO `game_player` (`game_id`, `player_id`) VALUES
('G1772807029000', 'P1772807029000'),
('G1772807029000', 'P1772807029001'),
('G1772807029000', 'P1772807029002'),
('G1772807029001', 'P1772807029003'),
('G1772807029001', 'P1772807029004');

-- 插入游戏公共牌关联数据
INSERT INTO `game_community_card` (`game_id`, `card_id`, `position`) VALUES
('G1772807029000', 'C001', 0),
('G1772807029000', 'C002', 1),
('G1772807029000', 'C003', 2),
('G1772807029001', 'C004', 0),
('G1772807029001', 'C005', 1),
('G1772807029001', 'C006', 2),
('G1772807029001', 'C007', 3);

-- 插入游戏动作数据
INSERT INTO `game_action` (`game_id`, `player_id`, `action_type`, `amount`, `action_time`) VALUES
('G1772807029000', 'P1772807029000', 'SMALL_BLIND', 10, NOW()),
('G1772807029000', 'P1772807029001', 'BIG_BLIND', 20, NOW()),
('G1772807029000', 'P1772807029002', 'CALL', 20, NOW()),
('G1772807029000', 'P1772807029000', 'CALL', 10, NOW()),
('G1772807029001', 'P1772807029003', 'SMALL_BLIND', 20, NOW()),
('G1772807029001', 'P1772807029004', 'BIG_BLIND', 40, NOW()),
('G1772807029001', 'P1772807029003', 'RAISE', 100, NOW()),
('G1772807029001', 'P1772807029004', 'CALL', 80, NOW());

-- 插入赛事测试数据
INSERT INTO `tournament` (`id`, `name`, `start_time`, `end_time`, `max_tables`, `small_blind`, `big_blind`, `blind_limit`, `is_running`, `status`) VALUES
('T1772807029000', '周末德州扑克大赛', NOW(), NULL, 5, 10, 20, 1000, TRUE, 'RUNNING'),
('T1772807029001', '新手德州扑克赛', NULL, NULL, 3, 5, 10, 500, FALSE, 'CREATED'),
('T1772807029002', '高手德州扑克挑战赛', NOW(), NULL, 4, 20, 40, 2000, TRUE, 'RUNNING');

-- 插入赛事桌台关联数据
INSERT INTO `tournament_table` (`tournament_id`, `table_id`, `add_time`) VALUES
('T1772807029000', 'TBL001', NOW()),
('T1772807029000', 'TBL002', NOW()),
('T1772807029000', 'TBL003', NOW()),
('T1772807029001', 'TBL004', NOW()),
('T1772807029001', 'TBL005', NOW()),
('T1772807029002', 'TBL001', NOW()),
('T1772807029002', 'TBL003', NOW());

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

-- 插入兑换物品测试数据
INSERT INTO `exchange_item` (`id`, `name`, `points`, `stock`, `type`) VALUES
('EI001', '德州扑克筹码 1000', 100, 100, '筹码'),
('EI002', '德州扑克筹码 5000', 450, 50, '筹码'),
('EI003', '德州扑克筹码 10000', 800, 30, '筹码'),
('EI004', '德州扑克纪念T恤', 2000, 20, '实物'),
('EI005', '德州扑克扑克牌', 500, 50, '实物');

-- 插入兑换申请测试数据
INSERT INTO `exchange_request` (`id`, `player_id`, `item_id`, `request_time`, `process_time`, `status`) VALUES
('ER001', 'P1772807029000', 'EI001', NOW(), NOW(), 'APPROVED'),
('ER002', 'P1772807029001', 'EI002', NOW(), NULL, 'PENDING'),
('ER003', 'P1772807029002', 'EI003', NOW(), NULL, 'PENDING'),
('ER004', 'P1772807029003', 'EI004', NOW(), NOW(), 'REJECTED'),
('ER005', 'P1772807029004', 'EI005', NOW(), NULL, 'PENDING');

-- 插入游戏历史测试数据
INSERT INTO `game_history` (`id`, `table_id`, `game_id`, `start_time`, `end_time`, `pot`, `winner_id`, `player_result`) VALUES
('GH001', 'TBL001', 'G1772807029000', NOW(), NOW(), 500, 'P1772807029000', 500),
('GH002', 'TBL003', 'G1772807029001', NOW(), NULL, 1000, NULL, 0);

-- 插入玩家会话测试数据
INSERT INTO `player_session` (`id`, `player_id`, `ip_address`, `device_info`, `create_time`, `last_access_time`) VALUES
('PS001', 'P1772807029000', '192.168.1.100', 'Windows 10, Chrome', UNIX_TIMESTAMP(NOW()) * 1000, UNIX_TIMESTAMP(NOW()) * 1000),
('PS002', 'P1772807029001', '192.168.1.101', 'MacOS, Safari', UNIX_TIMESTAMP(NOW()) * 1000, UNIX_TIMESTAMP(NOW()) * 1000),
('PS003', 'P1772807029002', '192.168.1.102', 'Linux, Firefox', UNIX_TIMESTAMP(NOW()) * 1000, UNIX_TIMESTAMP(NOW()) * 1000),
('PS004', 'P1772807029003', '192.168.1.103', 'Android, Chrome', UNIX_TIMESTAMP(NOW()) * 1000, UNIX_TIMESTAMP(NOW()) * 1000),
('PS005', 'P1772807029004', '192.168.1.104', 'iOS, Safari', UNIX_TIMESTAMP(NOW()) * 1000, UNIX_TIMESTAMP(NOW()) * 1000);

-- 插入用户协议测试数据
INSERT INTO `user_agreement` (`player_id`, `accepted`, `accept_time`) VALUES
('P1772807029000', TRUE, UNIX_TIMESTAMP(NOW()) * 1000),
('P1772807029001', TRUE, UNIX_TIMESTAMP(NOW()) * 1000),
('P1772807029002', TRUE, UNIX_TIMESTAMP(NOW()) * 1000),
('P1772807029003', TRUE, UNIX_TIMESTAMP(NOW()) * 1000),
('P1772807029004', TRUE, UNIX_TIMESTAMP(NOW()) * 1000);
