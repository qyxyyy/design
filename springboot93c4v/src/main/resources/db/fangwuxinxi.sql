-- 房屋信息表（fangwuxinxi）
CREATE TABLE IF NOT EXISTS `fangwuxinxi` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `fangwubianhao` varchar(100) DEFAULT NULL COMMENT '房屋编号',
  `fangyuanleixing` varchar(100) DEFAULT NULL COMMENT '房源类型',
  `fangwuhuxing` varchar(100) DEFAULT NULL COMMENT '房屋户型',
  `tupian` varchar(500) DEFAULT NULL COMMENT '图片',
  `zhuangtai` varchar(50) DEFAULT NULL COMMENT '状态',
  `mianji` varchar(50) DEFAULT NULL COMMENT '面积',
  `zujia` int(11) DEFAULT NULL COMMENT '租价',
  `huzhuxingming` varchar(100) DEFAULT NULL COMMENT '户主姓名',
  `lianxifangshi` varchar(100) DEFAULT NULL COMMENT '联系方式',
  `fabushijian` date DEFAULT NULL COMMENT '发布时间',
  `suozaishengfen` varchar(100) DEFAULT NULL COMMENT '所在省份',
  `suozaichengshi` varchar(100) DEFAULT NULL COMMENT '所在城市',
  `suozaidiqu` varchar(100) DEFAULT NULL COMMENT '所在地区',
  `xiangxidizhi` varchar(255) DEFAULT NULL COMMENT '详细地址',
  `xiangqing` text COMMENT '详情',
  `clicktime` datetime DEFAULT NULL COMMENT '最近点击时间',
  `clicknum` int(11) DEFAULT '0' COMMENT '点击次数',
  `addtime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  PRIMARY KEY (`id`),
  KEY `idx_fangwubianhao` (`fangwubianhao`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='房屋信息';

