-- ************************** 房屋租赁系统数据库 **************************
-- 临时关闭外键检查（解决DROP TABLE因外键关联报错的问题）
SET FOREIGN_KEY_CHECKS = 0;

-- 创建数据库（指定字符集和排序规则，避免中文乱码）
CREATE DATABASE IF NOT EXISTS house_rental_system
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;

-- 使用该数据库
USE house_rental_system;

-- ----------------------------
-- 1. 用户表（区分租客、房东、管理员）
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
                        `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID（主键）',
                        `username` VARCHAR(50) NOT NULL COMMENT '用户名（登录账号）',
                        `password` VARCHAR(100) NOT NULL COMMENT '密码（建议加密存储，示例用MD5）',
                        `real_name` VARCHAR(20) DEFAULT '' COMMENT '真实姓名',
                        `phone` VARCHAR(11) DEFAULT '' COMMENT '手机号',
                        `email` VARCHAR(50) DEFAULT '' COMMENT '邮箱',
                        `avatar` VARCHAR(255) DEFAULT '' COMMENT '头像URL',
                        `identity_type` TINYINT NOT NULL COMMENT '身份类型：1-租客 2-房东 3-管理员',
                        `gender` TINYINT DEFAULT 0 COMMENT '性别：0-未知 1-男 2-女',
                        `address` VARCHAR(255) DEFAULT '' COMMENT '常住地址',
                        `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
                        `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_username` (`username`),
                        UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 2. 房东资质表（房东上传的证明资料）
-- ----------------------------
DROP TABLE IF EXISTS `landlord_qualification`;
CREATE TABLE `landlord_qualification` (
                                          `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '资质ID（主键）',
                                          `landlord_id` INT UNSIGNED NOT NULL COMMENT '房东ID（关联user表）',
                                          `id_card_front` VARCHAR(255) NOT NULL COMMENT '身份证正面URL',
                                          `id_card_back` VARCHAR(255) NOT NULL COMMENT '身份证反面URL',
                                          `house_ownership` VARCHAR(255) DEFAULT '' COMMENT '房产证URL',
                                          `audit_status` TINYINT DEFAULT 0 COMMENT '审核状态：0-待审核 1-审核通过 2-审核驳回',
                                          `audit_remark` VARCHAR(255) DEFAULT '' COMMENT '审核备注',
                                          `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
                                          `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
                                          PRIMARY KEY (`id`),
                                          KEY `idx_landlord_id` (`landlord_id`),
                                          CONSTRAINT `fk_qualification_landlord` FOREIGN KEY (`landlord_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房东资质表';

-- ----------------------------
-- 3. 标签表（房屋标签/用户偏好标签）
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
                       `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '标签ID（主键）',
                       `tag_name` VARCHAR(30) NOT NULL COMMENT '标签名称（如：押一付一、近地铁、朝南）',
                       `tag_type` TINYINT DEFAULT 1 COMMENT '标签类型：1-房屋属性 2-支付方式 3-位置优势',
                       `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
                       `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                       PRIMARY KEY (`id`),
                       UNIQUE KEY `uk_tag_name` (`tag_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- ----------------------------
-- 4. 房屋信息表（整合长租/短租，用type区分）
-- ----------------------------
DROP TABLE IF EXISTS `house`;
CREATE TABLE `house` (
                         `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '房屋ID（主键）',
                         `landlord_id` INT UNSIGNED NOT NULL COMMENT '房东ID（关联user表）',
                         `house_title` VARCHAR(100) NOT NULL COMMENT '房屋标题',
                         `house_type` TINYINT NOT NULL COMMENT '租赁类型：1-长租 2-短租',
                         `rent_price` DECIMAL(10,2) NOT NULL COMMENT '租金（元/月）',
                         `house_area` DECIMAL(8,2) NOT NULL COMMENT '房屋面积（㎡）',
                         `house_address` VARCHAR(255) NOT NULL COMMENT '房屋详细地址',
                         `city` VARCHAR(20) NOT NULL COMMENT '所在城市',
                         `district` VARCHAR(20) NOT NULL COMMENT '所在区域',
                         `room_count` TINYINT NOT NULL COMMENT '房间数',
                         `hall_count` TINYINT NOT NULL COMMENT '客厅数',
                         `toilet_count` TINYINT NOT NULL COMMENT '卫生间数',
                         `floor` VARCHAR(10) DEFAULT '' COMMENT '楼层（如：5/18）',
                         `house_desc` TEXT DEFAULT '' COMMENT '房屋描述',
                         `house_pics` VARCHAR(1000) DEFAULT '' COMMENT '房屋图片URL（多个用逗号分隔）',
                         `view_count` INT UNSIGNED DEFAULT 0 COMMENT '浏览量',
                         `collect_count` INT UNSIGNED DEFAULT 0 COMMENT '收藏量',
                         `audit_status` TINYINT DEFAULT 0 COMMENT '审核状态：0-待审核 1-审核通过 2-审核驳回',
                         `is_on_shelf` TINYINT DEFAULT 0 COMMENT '是否上架：0-下架 1-上架',
                         `release_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
                         `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                         PRIMARY KEY (`id`),
                         KEY `idx_landlord_id` (`landlord_id`),
                         KEY `idx_house_type` (`house_type`),
                         KEY `idx_view_collect` (`view_count`,`collect_count`) COMMENT '用于首页推荐排序',
                         KEY `idx_audit_shelf` (`audit_status`,`is_on_shelf`) COMMENT '筛选上架且审核通过的房源',
                         CONSTRAINT `fk_house_landlord` FOREIGN KEY (`landlord_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房屋信息表';

-- ----------------------------
-- 5. 房屋标签关联表（房屋与标签多对多）
-- ----------------------------
DROP TABLE IF EXISTS `house_tag`;
CREATE TABLE `house_tag` (
                             `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '关联ID（主键）',
                             `house_id` INT UNSIGNED NOT NULL COMMENT '房屋ID（关联house表）',
                             `tag_id` INT UNSIGNED NOT NULL COMMENT '标签ID（关联tag表）',
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `uk_house_tag` (`house_id`,`tag_id`) COMMENT '避免重复关联',
                             KEY `idx_house_id` (`house_id`),
                             KEY `idx_tag_id` (`tag_id`),
                             CONSTRAINT `fk_house_tag_house` FOREIGN KEY (`house_id`) REFERENCES `house` (`id`) ON DELETE CASCADE,
                             CONSTRAINT `fk_house_tag_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房屋标签关联表';

-- ----------------------------
-- 6. 用户偏好标签关联表（用户与偏好标签多对多）
-- ----------------------------
DROP TABLE IF EXISTS `user_preference_tag`;
CREATE TABLE `user_preference_tag` (
                                       `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '关联ID（主键）',
                                       `user_id` INT UNSIGNED NOT NULL COMMENT '用户ID（关联user表）',
                                       `tag_id` INT UNSIGNED NOT NULL COMMENT '标签ID（关联tag表）',
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `uk_user_tag` (`user_id`,`tag_id`) COMMENT '避免重复偏好',
                                       KEY `idx_user_id` (`user_id`),
                                       KEY `idx_tag_id` (`tag_id`),
                                       CONSTRAINT `fk_user_preference_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                                       CONSTRAINT `fk_user_preference_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户偏好标签关联表';

-- ----------------------------
-- 7. 浏览记录表（统计用户浏览房屋的记录）
-- ----------------------------
DROP TABLE IF EXISTS `browse_record`;
CREATE TABLE `browse_record` (
                                 `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID（主键）',
                                 `user_id` INT UNSIGNED NOT NULL COMMENT '用户ID（关联user表）',
                                 `house_id` INT UNSIGNED NOT NULL COMMENT '房屋ID（关联house表）',
                                 `browse_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
                                 PRIMARY KEY (`id`),
                                 KEY `idx_user_house` (`user_id`,`house_id`),
                                 KEY `idx_browse_time` (`browse_time`) COMMENT '用于统计近半个月浏览量',
                                 CONSTRAINT `fk_browse_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                                 CONSTRAINT `fk_browse_house` FOREIGN KEY (`house_id`) REFERENCES `house` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='浏览记录表';

-- ----------------------------
-- 8. 收藏表（用户收藏房屋）
-- ----------------------------
DROP TABLE IF EXISTS `collection`;
CREATE TABLE `collection` (
                              `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '收藏ID（主键）',
                              `user_id` INT UNSIGNED NOT NULL COMMENT '用户ID（关联user表）',
                              `house_id` INT UNSIGNED NOT NULL COMMENT '房屋ID（关联house表）',
                              `collect_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
                              `is_cancel` TINYINT DEFAULT 0 COMMENT '是否取消：0-未取消 1-已取消',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_user_house` (`user_id`,`house_id`,`is_cancel`) COMMENT '避免重复收藏',
                              KEY `idx_user_id` (`user_id`),
                              KEY `idx_house_id` (`house_id`),
                              CONSTRAINT `fk_collect_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                              CONSTRAINT `fk_collect_house` FOREIGN KEY (`house_id`) REFERENCES `house` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- ----------------------------
-- 9. 房屋评论表（用户对房屋的评论）
-- ----------------------------
DROP TABLE IF EXISTS `house_comment`;
CREATE TABLE `house_comment` (
                                 `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '评论ID（主键）',
                                 `user_id` INT UNSIGNED NOT NULL COMMENT '用户ID（关联user表）',
                                 `house_id` INT UNSIGNED NOT NULL COMMENT '房屋ID（关联house表）',
                                 `comment_content` TEXT NOT NULL COMMENT '评论内容',
                                 `comment_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
                                 `status` TINYINT DEFAULT 1 COMMENT '状态：0-删除 1-正常',
                                 PRIMARY KEY (`id`),
                                 KEY `idx_user_house` (`user_id`,`house_id`),
                                 CONSTRAINT `fk_house_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                                 CONSTRAINT `fk_house_comment_house` FOREIGN KEY (`house_id`) REFERENCES `house` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房屋评论表';

-- ----------------------------
-- 10. 论坛帖子表（租户交流平台）
-- ----------------------------
DROP TABLE IF EXISTS `forum_post`;
CREATE TABLE `forum_post` (
                              `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '帖子ID（主键）',
                              `user_id` INT UNSIGNED NOT NULL COMMENT '发帖人ID（关联user表）',
                              `post_title` VARCHAR(100) NOT NULL COMMENT '帖子标题',
                              `post_content` TEXT NOT NULL COMMENT '帖子内容',
                              `post_type` TINYINT DEFAULT 1 COMMENT '帖子类型：1-找合租室友 2-看房心得 3-其他',
                              `view_count` INT UNSIGNED DEFAULT 0 COMMENT '帖子浏览量',
                              `like_count` INT UNSIGNED DEFAULT 0 COMMENT '点赞数',
                              `status` TINYINT DEFAULT 1 COMMENT '状态：0-删除 1-正常 2-审核中',
                              `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发帖时间',
                              `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`id`),
                              KEY `idx_user_id` (`user_id`),
                              KEY `idx_post_type` (`post_type`),
                              CONSTRAINT `fk_forum_post_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛帖子表';

-- ----------------------------
-- 11. 论坛评论表（帖子的评论）
-- ----------------------------
DROP TABLE IF EXISTS `forum_comment`;
CREATE TABLE `forum_comment` (
                                 `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '评论ID（主键）',
                                 `post_id` INT UNSIGNED NOT NULL COMMENT '帖子ID（关联forum_post表）',
                                 `user_id` INT UNSIGNED NOT NULL COMMENT '评论人ID（关联user表）',
                                 `comment_content` TEXT NOT NULL COMMENT '评论内容',
                                 `parent_id` INT UNSIGNED DEFAULT 0 COMMENT '父评论ID（0表示一级评论）',
                                 `status` TINYINT DEFAULT 1 COMMENT '状态：0-删除 1-正常',
                                 `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
                                 PRIMARY KEY (`id`),
                                 KEY `idx_post_id` (`post_id`),
                                 KEY `idx_user_id` (`user_id`),
                                 KEY `idx_parent_id` (`parent_id`),
                                 CONSTRAINT `fk_forum_comment_post` FOREIGN KEY (`post_id`) REFERENCES `forum_post` (`id`) ON DELETE CASCADE,
                                 CONSTRAINT `fk_forum_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛评论表';

-- ----------------------------
-- 初始基础数据
-- ----------------------------
-- 1. 初始化标签数据
INSERT INTO `tag` (`tag_name`, `tag_type`) VALUES
                                               ('押一付一', 2),
                                               ('押一付三', 2),
                                               ('近地铁', 3),
                                               ('近公交站', 3),
                                               ('朝南', 1),
                                               ('朝北', 1),
                                               ('拎包入住', 1),
                                               ('独立卫浴', 1),
                                               ('可做饭', 1),
                                               ('无中介费', 2);

-- 2. 初始化管理员账号（用户名：admin，密码：123456（MD5加密后））
INSERT INTO `user` (`username`, `password`, `identity_type`, `status`) VALUES
    ('admin', 'e10adc3949ba59abbe56e057f20f883e', 3, 1);

-- 3. 测试数据（可选，可删除）
-- 测试租客
INSERT INTO `user` (`username`, `password`, `real_name`, `phone`, `identity_type`, `status`) VALUES
    ('tenant01', 'e10adc3949ba59abbe56e057f20f883e', '张三', '13800138000', 1, 1);
-- 测试房东
INSERT INTO `user` (`username`, `password`, `real_name`, `phone`, `identity_type`, `status`) VALUES
    ('landlord01', 'e10adc3949ba59abbe56e057f20f883e', '李四', '13900139000', 2, 1);
-- 测试房源
INSERT INTO `house` (`landlord_id`, `house_title`, `house_type`, `rent_price`, `house_area`, `house_address`, `city`, `district`, `room_count`, `hall_count`, `toilet_count`, `floor`, `house_desc`, `audit_status`, `is_on_shelf`) VALUES
    (2, '地铁口精装一室一厅 押一付一', 1, 2500.00, 60.00, 'XX市XX区XX路XX小区1栋501', '北京市', '朝阳区', 1, 1, 1, '5/18', '房子采光好，近地铁1号线，拎包入住', 1, 1);
-- 房源关联标签
INSERT INTO `house_tag` (`house_id`, `tag_id`) VALUES (1, 1), (1, 3), (1, 7);
-- 租客偏好标签
INSERT INTO `user_preference_tag` (`user_id`, `tag_id`) VALUES (1, 1), (1, 3);

-- 恢复外键检查（重要，保证后续数据一致性）
SET FOREIGN_KEY_CHECKS = 1;