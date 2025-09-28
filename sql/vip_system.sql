-- 会员系统相关表结构
-- 作者: Claude
-- 日期: 2025-09-28

-- 用户会员信息表
DROP TABLE IF EXISTS `sys_user_vip`;
CREATE TABLE `sys_user_vip` (
  `vip_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '会员ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `vip_type` varchar(50) DEFAULT NULL COMMENT '会员类型',
  `vip_level` int(11) DEFAULT '0' COMMENT '会员等级(0-普通用户,1-白银,2-黄金,3-铂金,4-钻石)',
  `subscribe_time` datetime DEFAULT NULL COMMENT '订阅时间',
  `expire_time` datetime DEFAULT NULL COMMENT '到期时间',
  `vip_status` char(1) DEFAULT '0' COMMENT '会员状态(0-停用,1-正常)',
  `custom_role_quota` int(11) DEFAULT '0' COMMENT '定制角色配额',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`vip_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_vip_status` (`vip_status`),
  KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户会员信息表';

-- 会员等级配置表
DROP TABLE IF EXISTS `sys_vip_level`;
CREATE TABLE `sys_vip_level` (
  `level_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '等级ID',
  `level` int(11) NOT NULL COMMENT '等级数值',
  `level_name` varchar(50) NOT NULL COMMENT '等级名称',
  `level_desc` varchar(200) DEFAULT NULL COMMENT '等级描述',
  `monthly_price` int(11) DEFAULT '0' COMMENT '月价格(分)',
  `quarterly_price` int(11) DEFAULT '0' COMMENT '季度价格(分)',
  `yearly_price` int(11) DEFAULT '0' COMMENT '年价格(分)',
  `daily_chat_limit` int(11) DEFAULT '10' COMMENT '每日聊天限制(-1为无限)',
  `daily_image_limit` int(11) DEFAULT '0' COMMENT '每日图片生成限制(-1为无限)',
  `custom_role_quota` int(11) DEFAULT '0' COMMENT '定制角色配额(-1为无限)',
  `voice_enabled` tinyint(1) DEFAULT '0' COMMENT '语音功能启用(0-关闭,1-开启)',
  `image_enabled` tinyint(1) DEFAULT '0' COMMENT '图片生成启用(0-关闭,1-开启)',
  `exclusive_role_enabled` tinyint(1) DEFAULT '0' COMMENT '专属角色启用(0-关闭,1-开启)',
  `chat_export_enabled` tinyint(1) DEFAULT '0' COMMENT '聊天导出启用(0-关闭,1-开启)',
  `priority_support_enabled` tinyint(1) DEFAULT '0' COMMENT '优先客服启用(0-关闭,1-开启)',
  `status` char(1) DEFAULT '0' COMMENT '状态(0-正常,1-停用)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`level_id`),
  UNIQUE KEY `uk_level` (`level`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='会员等级配置表';

-- 插入默认会员等级配置
INSERT INTO `sys_vip_level` VALUES
(1, 0, '普通用户', '免费用户，基础功能', 0, 0, 0, 10, 0, 0, 0, 0, 0, 0, 0, '0', 'admin', NOW(), 'admin', NOW()),
(2, 1, '白银会员', '入门会员，享受语音聊天', 1999, 4999, 15999, 50, 0, 2, 1, 0, 0, 0, 1, '0', 'admin', NOW(), 'admin', NOW()),
(3, 2, '黄金会员', '热门会员，图片生成+聊天导出', 2999, 7999, 25999, 150, 10, 5, 1, 1, 0, 1, 1, '0', 'admin', NOW(), 'admin', NOW()),
(4, 3, '铂金会员', '高级会员，专属角色体验', 4999, 12999, 39999, 500, 30, 10, 1, 1, 1, 1, 1, '0', 'admin', NOW(), 'admin', NOW()),
(5, 4, '钻石会员', '尊享会员，无限制使用', 9999, 24999, 79999, -1, -1, -1, 1, 1, 1, 1, 1, '0', 'admin', NOW(), 'admin', NOW());

-- 用户每日使用统计表（可选，用于统计每日使用次数）
DROP TABLE IF EXISTS `sys_user_daily_usage`;
CREATE TABLE `sys_user_daily_usage` (
  `usage_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '使用记录ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `usage_date` date NOT NULL COMMENT '使用日期',
  `chat_count` int(11) DEFAULT '0' COMMENT '聊天次数',
  `image_count` int(11) DEFAULT '0' COMMENT '图片生成次数',
  `voice_count` int(11) DEFAULT '0' COMMENT '语音使用次数',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`usage_id`),
  UNIQUE KEY `uk_user_date` (`user_id`, `usage_date`),
  KEY `idx_usage_date` (`usage_date`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户每日使用统计表';