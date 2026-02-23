-- 论坛表
CREATE TABLE IF NOT EXISTS `luntan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(200) DEFAULT NULL COMMENT '标题',
  `content` text COMMENT '内容',
  `userid` bigint(20) DEFAULT NULL COMMENT '发帖人id',
  `username` varchar(64) DEFAULT NULL COMMENT '发帖人',
  `addtime` datetime DEFAULT NULL COMMENT '发帖时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='论坛';
