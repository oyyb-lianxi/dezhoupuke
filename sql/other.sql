-- 创建兑换物品表
CREATE TABLE IF NOT EXISTS `exchange_item` (
  `id` VARCHAR(50) PRIMARY KEY COMMENT '兑换物品唯一标识符',
  `name` VARCHAR(100) NOT NULL COMMENT '兑换物品名称',
  `points` INT NOT NULL COMMENT '兑换所需积分',
  `stock` INT NOT NULL COMMENT '库存数量',
  `type` VARCHAR(50) NOT NULL COMMENT '物品类型'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='兑换物品表';

-- 创建兑换申请表
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

-- 创建游戏历史表
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

-- 创建玩家会话表
CREATE TABLE IF NOT EXISTS `player_session` (
  `id` VARCHAR(50) PRIMARY KEY COMMENT '会话唯一标识符',
  `player_id` VARCHAR(50) NOT NULL COMMENT '玩家唯一标识符',
  `ip_address` VARCHAR(50) NOT NULL COMMENT 'IP地址',
  `device_info` VARCHAR(255) COMMENT '设备信息',
  `create_time` BIGINT NOT NULL COMMENT '创建时间（毫秒）',
  `last_access_time` BIGINT NOT NULL COMMENT '最后访问时间（毫秒）',
  FOREIGN KEY (`player_id`) REFERENCES `player` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家会话表';

-- 创建用户协议表
CREATE TABLE IF NOT EXISTS `user_agreement` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '协议ID',
  `player_id` VARCHAR(50) NOT NULL COMMENT '玩家唯一标识符',
  `accepted` BOOLEAN NOT NULL COMMENT '是否接受用户协议',
  `accept_time` BIGINT NOT NULL COMMENT '接受时间（毫秒）',
  FOREIGN KEY (`player_id`) REFERENCES `player` (`id`) ON DELETE CASCADE,
  UNIQUE KEY `uk_player_agreement` (`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户协议表';

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
