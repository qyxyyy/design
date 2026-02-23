-- 户主表增加登录用账号、密码字段（用于房东登录注册）
-- 在数据库 fangwu 中执行此脚本

USE fangwu;

ALTER TABLE huzhu ADD COLUMN zhanghao VARCHAR(50) DEFAULT NULL COMMENT '账号';
ALTER TABLE huzhu ADD COLUMN mima VARCHAR(50) DEFAULT NULL COMMENT '密码';
