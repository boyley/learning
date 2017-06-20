Create Database If Not Exists seckill Character Set UTF8;
USE seckill;

DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `authority`;
DROP TABLE IF EXISTS `persistent_audit_event`;
DROP TABLE IF EXISTS `persistent_audit_evt_data`;
DROP TABLE IF EXISTS `persistent_token`;
DROP TABLE IF EXISTS `user_authority`;
DROP TABLE IF EXISTS `receiver_address`;

-- ----------------------------
-- Table structure for seckill
-- ----------------------------
CREATE TABLE `receiver_address` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `address` varchar(50) DEFAULT NULL COMMENT '地址',
  `use` bit(1) DEFAULT b'0' COMMENT '是否是常用地址',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='收货地址';

CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `password_hash` varchar(100) DEFAULT NULL COMMENT '密码',
  `account_non_expired` bit(1) DEFAULT NULL COMMENT '账号是否未过期',
  `account_non_locked` bit(1) DEFAULT NULL COMMENT '账号是否未锁定',
  `credentials_non_expired` bit(1) DEFAULT NULL COMMENT '密码是否为过期',
  `enabled` bit(1) DEFAULT NULL COMMENT '账号是否可用',
  `activated` bit(1) DEFAULT NULL COMMENT '账号是否激活',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='用户表';

CREATE TABLE `authority` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `authority` varchar(20) DEFAULT NULL COMMENT '权限名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='用户权限表';


CREATE TABLE `persistent_audit_event` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `principal` varchar(20) DEFAULT NULL COMMENT '用户对象',
  `event_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '事件产生的时间',
  `event_type` varchar(20) DEFAULT NULL COMMENT '时间类型',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='序列化监听事件';


CREATE TABLE `persistent_audit_evt_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `event_id` int(11) DEFAULT NULL COMMENT '事件记录ID',
  `name` varchar(30) DEFAULT NULL COMMENT '事件名称',
  `value` varchar(30) DEFAULT NULL COMMENT '事件值',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='事件信息';

CREATE TABLE `persistent_token` (
  `series` varchar(100) NOT NULL COMMENT 'series',
  `token_value` varchar(100) DEFAULT NULL COMMENT 'tokenValue',
  `token_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'token产生的时间',
  `ip_address` varchar(32) DEFAULT NULL COMMENT 'ipAddress',
  `user_agent` varchar(32) DEFAULT NULL COMMENT 'userAgent',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  PRIMARY KEY (`series`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='序列化token';

CREATE TABLE `user_authority` (
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `authority_id` int(11) DEFAULT NULL COMMENT '角色ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户权限管理表';