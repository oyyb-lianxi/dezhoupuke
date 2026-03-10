-- 创建游戏表
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

-- 创建游戏玩家关联表
CREATE TABLE IF NOT EXISTS `game_player` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
  `game_id` VARCHAR(50) NOT NULL COMMENT '游戏ID',
  `player_id` VARCHAR(50) NOT NULL COMMENT '玩家ID',
  FOREIGN KEY (`game_id`) REFERENCES `game` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`player_id`) REFERENCES `player` (`id`) ON DELETE CASCADE,
  UNIQUE KEY `uk_game_player` (`game_id`, `player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='游戏玩家关联表';

-- 创建卡牌表
CREATE TABLE IF NOT EXISTS `card` (
  `id` VARCHAR(50) PRIMARY KEY COMMENT '卡牌唯一标识符',
  `suit` VARCHAR(20) NOT NULL COMMENT '花色（如Hearts、Diamonds、Clubs、Spades）',
  `rank` VARCHAR(10) NOT NULL COMMENT '点数（如A、2-10、J、Q、K）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='卡牌表';

-- 创建游戏公共牌关联表
CREATE TABLE IF NOT EXISTS `game_community_card` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
  `game_id` VARCHAR(50) NOT NULL COMMENT '游戏ID',
  `card_id` VARCHAR(50) NOT NULL COMMENT '卡牌ID',
  `position` INT NOT NULL COMMENT '位置',
  FOREIGN KEY (`game_id`) REFERENCES `game` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`card_id`) REFERENCES `card` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='游戏公共牌关联表';

-- 创建游戏动作表
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
