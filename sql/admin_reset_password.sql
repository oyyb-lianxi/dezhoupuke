-- 新增admin管理员到user表
INSERT INTO `user` (`id`, `id_card`, `password`, `name`, `create_time`, `last_login_time`, `status`) VALUES
('U1772807029005', '432524199601110039', '123456', 'admin', NOW(), NOW(), 'ACTIVE');

-- 将所有用户密码重置为123456
UPDATE `user` SET `password` = '123456';
