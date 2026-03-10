-- 创建赛事表
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

-- 创建赛事桌台关联表
CREATE TABLE IF NOT EXISTS `tournament_table` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
  `tournament_id` VARCHAR(50) NOT NULL COMMENT '赛事ID',
  `table_id` VARCHAR(50) NOT NULL COMMENT '桌台ID',
  `add_time` DATETIME NOT NULL DEFAULT NOW() COMMENT '添加时间',
  FOREIGN KEY (`tournament_id`) REFERENCES `tournament` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`table_id`) REFERENCES `table` (`id`) ON DELETE CASCADE,
  UNIQUE KEY `uk_tournament_table` (`tournament_id`, `table_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛事桌台关联表';

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
