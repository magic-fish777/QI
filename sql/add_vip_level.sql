-- 添加vip_level字段到现有的sys_user_vip表
ALTER TABLE `sys_user_vip`
ADD COLUMN `vip_level` int DEFAULT '0' COMMENT '会员等级(0-普通用户,1-白银,2-黄金,3-铂金,4-钻石)'
AFTER `vip_type`;