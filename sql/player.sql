-- 创建玩家表
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

-- 插入测试数据
INSERT INTO `player` (`id`, `nickname`, `avatar`, `warehouse_points`, `table_points`, `pending_points`, `level_experience`, `total_points`, `level`, `is_online`, `is_spectating`, `last_login_time`, `registration_time`, `is_verified`, `age`, `ip_address`, `device_info`, `user_id`) VALUES
('P1772807029000', '德州高手', 'avatar1.png', 10000, 5000, 0, 100, 15000, 5, TRUE, FALSE, NOW(), NOW(), TRUE, 25, '192.168.1.100', 'Windows 10, Chrome', 'U1772807029000'),
('P1772807029001', '扑克大师', 'avatar2.png', 8000, 3000, 0, 80, 11000, 4, TRUE, FALSE, NOW(), NOW(), TRUE, 30, '192.168.1.101', 'MacOS, Safari', 'U1772807029001'),
('P1772807029002', '赌神', 'avatar3.png', 12000, 0, 0, 120, 12000, 5, FALSE, FALSE, NOW(), NOW(), TRUE, 28, '192.168.1.102', 'Linux, Firefox', 'U1772807029002'),
('P1772807029003', '新手小白', 'avatar4.png', 5000, 2000, 0, 20, 7000, 2, TRUE, FALSE, NOW(), NOW(), TRUE, 20, '192.168.1.103', 'Android, Chrome', 'U1772807029003'),
('P1772807029004', '老油条', 'avatar5.png', 15000, 8000, 0, 150, 23000, 6, FALSE, FALSE, NOW(), NOW(), TRUE, 35, '192.168.1.104', 'iOS, Safari', 'U1772807029004');
