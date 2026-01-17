-- ==========================================
-- 新年祝福全国地级市互动平台 - MySQL数据库初始化脚本
-- 版本: 1.0
-- 创建时间: 2026-01-16
-- ==========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `newyear_blessing`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `newyear_blessing`;

-- ==========================================
-- 1. 用户祝福表（核心业务表）
-- ==========================================
CREATE TABLE `user_blessing`
(
    `id`               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `nickname`         VARCHAR(50)     NOT NULL DEFAULT '匿名用户' COMMENT '用户昵称',
    `blessing_content` TEXT            NOT NULL COMMENT '祝福内容',
    `image_name`       VARCHAR(100)             DEFAULT NULL COMMENT 'MinIO存储的图片文件名',
    `city`             VARCHAR(50)              DEFAULT NULL COMMENT '用户所在城市',
    `audit_status`     TINYINT(2)      NOT NULL DEFAULT 0 COMMENT '审核状态(0:待审核 1:审核通过 2:审核驳回)',
    `audit_reason`     VARCHAR(500)             DEFAULT NULL COMMENT '审核驳回原因',
    `emotion_score`    DECIMAL(5, 2)            DEFAULT NULL COMMENT '情感得分(0-100)',
    `submit_ip`        VARCHAR(50)              DEFAULT NULL COMMENT '提交IP地址',
    `submit_time`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    `audit_time`       DATETIME                 DEFAULT NULL COMMENT '审核时间',
    `is_deleted`       TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '是否删除(0:否 1:是)',
    `created_time`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_audit_status` (`audit_status`),
    KEY `idx_submit_time` (`submit_time` DESC),
    KEY `idx_is_deleted` (`is_deleted`),
    KEY `idx_city` (`city`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户祝福表';

-- ==========================================
-- 3. 图片资源表
-- ==========================================
CREATE TABLE `blessing_image`
(
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `blessing_id`   BIGINT UNSIGNED NOT NULL COMMENT '关联祝福ID',
    `image_name`    VARCHAR(100)    NOT NULL COMMENT 'MinIO存储的图片文件名',
    `original_name` VARCHAR(200)             DEFAULT NULL COMMENT '原始文件名（仅用于日志追溯）',
    `image_size`    BIGINT UNSIGNED NOT NULL COMMENT '图片大小(字节)',
    `image_format`  VARCHAR(10)     NOT NULL COMMENT '图片格式(jpg/png/webp)',
    `minio_bucket`  VARCHAR(50)     NOT NULL COMMENT 'MinIO存储桶名称',
    `minio_path`    VARCHAR(500)    NOT NULL COMMENT 'MinIO完整路径',
    `access_count`  INT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '访问次数统计',
    `is_deleted`    TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '是否删除(0:否 1:是)',
    `created_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_image_name` (`image_name`),
    KEY `idx_blessing_id` (`blessing_id`),
    KEY `idx_is_deleted` (`is_deleted`),
    KEY `idx_created_time` (`created_time`),
    CONSTRAINT `fk_image_blessing` FOREIGN KEY (`blessing_id`) REFERENCES `user_blessing` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='图片资源表';

-- ==========================================
-- 4. 审核日志表
-- ==========================================
CREATE TABLE `audit_log`
(
    `id`                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `blessing_id`       BIGINT UNSIGNED NOT NULL COMMENT '关联祝福ID',
    `audit_type`        TINYINT(2)      NOT NULL COMMENT '审核类型(1:内容审核 2:图片审核)',
    `audit_result`      TINYINT(2)      NOT NULL COMMENT '审核结果(1:通过 2:驳回)',
    `audit_reason`      VARCHAR(500)             DEFAULT NULL COMMENT '审核原因/驳回理由',
    `deepseek_request`  TEXT                     DEFAULT NULL COMMENT 'DeepSeek API请求内容',
    `deepseek_response` TEXT                     DEFAULT NULL COMMENT 'DeepSeek API响应内容',
    `emotion_score`     DECIMAL(5, 2)            DEFAULT NULL COMMENT '情感得分',
    `audit_duration`    INT UNSIGNED             DEFAULT NULL COMMENT '审核耗时(毫秒)',
    `audit_time`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
    PRIMARY KEY (`id`),
    KEY `idx_blessing_id` (`blessing_id`),
    KEY `idx_audit_time` (`audit_time` DESC),
    KEY `idx_audit_result` (`audit_result`),
    CONSTRAINT `fk_audit_blessing` FOREIGN KEY (`blessing_id`) REFERENCES `user_blessing` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='审核日志表';

-- ==========================================
-- 5. 系统操作日志表
-- ==========================================
CREATE TABLE `system_log`
(
    `id`             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `log_type`       VARCHAR(50)     NOT NULL COMMENT '日志类型(API_ACCESS/IMAGE_UPLOAD/ERROR等)',
    `log_level`      VARCHAR(20)     NOT NULL COMMENT '日志级别(INFO/WARN/ERROR)',
    `module_name`    VARCHAR(50)     NOT NULL COMMENT '模块名称',
    `operation`      VARCHAR(100)    NOT NULL COMMENT '操作描述',
    `request_url`    VARCHAR(500)             DEFAULT NULL COMMENT '请求URL',
    `request_method` VARCHAR(10)              DEFAULT NULL COMMENT '请求方法(GET/POST等)',
    `request_params` TEXT                     DEFAULT NULL COMMENT '请求参数',
    `response_code`  VARCHAR(20)              DEFAULT NULL COMMENT '响应状态码',
    `error_message`  TEXT                     DEFAULT NULL COMMENT '错误信息',
    `ip_address`     VARCHAR(50)              DEFAULT NULL COMMENT 'IP地址',
    `user_agent`     VARCHAR(500)             DEFAULT NULL COMMENT '用户代理',
    `execution_time` INT UNSIGNED             DEFAULT NULL COMMENT '执行耗时(毫秒)',
    `created_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_log_type` (`log_type`),
    KEY `idx_log_level` (`log_level`),
    KEY `idx_created_time` (`created_time` DESC),
    KEY `idx_composite` (`log_type`, `log_level`, `created_time` DESC)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='系统操作日志表';

-- ==========================================
-- 6. 接口访问限流记录表
-- ==========================================
CREATE TABLE `rate_limit_record`
(
    `id`                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `ip_address`        VARCHAR(50)     NOT NULL COMMENT 'IP地址',
    `api_path`          VARCHAR(200)    NOT NULL COMMENT 'API路径',
    `request_count`     INT UNSIGNED    NOT NULL DEFAULT 1 COMMENT '请求次数',
    `last_request_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后请求时间',
    `is_blocked`        TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '是否被封禁(0:否 1:是)',
    `block_expire_time` DATETIME                 DEFAULT NULL COMMENT '封禁过期时间',
    `created_time`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_ip_api` (`ip_address`, `api_path`),
    KEY `idx_ip_address` (`ip_address`),
    KEY `idx_is_blocked` (`is_blocked`),
    KEY `idx_last_request` (`last_request_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='接口访问限流记录表';

-- ==========================================
-- 7. 系统配置表
-- ==========================================
CREATE TABLE `system_config`
(
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `config_key`   VARCHAR(100)    NOT NULL COMMENT '配置键',
    `config_value` TEXT            NOT NULL COMMENT '配置值',
    `config_desc`  VARCHAR(200)             DEFAULT NULL COMMENT '配置描述',
    `config_type`  VARCHAR(20)     NOT NULL DEFAULT 'STRING' COMMENT '配置类型(STRING/NUMBER/BOOLEAN/JSON)',
    `is_system`    TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '是否系统配置(1:是 0:否)',
    `created_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='系统配置表';

-- ==========================================
-- 插入系统配置初始数据
-- ==========================================
INSERT INTO `system_config` (`config_key`, `config_value`, `config_desc`, `config_type`, `is_system`)
VALUES ('image.max.size', '5242880', '图片最大上传大小(字节) 5MB', 'NUMBER', 1),
       ('image.allowed.formats', 'jpg,png,webp', '允许上传的图片格式', 'STRING', 1),
       ('blessing.min.length', '1', '祝福内容最小长度', 'NUMBER', 1),
       ('blessing.max.length', '200', '祝福内容最大长度', 'NUMBER', 1),
       ('deepseek.api.timeout', '5000', 'DeepSeek API超时时间(毫秒)', 'NUMBER', 1),
       ('deepseek.emotion.threshold', '60', '情感得分通过阈值(0-100)', 'NUMBER', 1),
       ('rate.limit.per.minute', '10', '每分钟请求限制次数', 'NUMBER', 1),
       ('data.retention.days', '365', '数据保留天数', 'NUMBER', 1),
       ('cache.expire.seconds', '3600', '缓存过期时间(秒)', 'NUMBER', 1);

-- ==========================================
-- 创建存储过程：清理过期数据
-- ==========================================
DELIMITER $$

CREATE PROCEDURE `sp_clean_expired_data`()
BEGIN
    DECLARE v_retention_days INT;
    DECLARE v_expire_date DATETIME;

    -- 获取数据保留天数配置
    SELECT CAST(config_value AS UNSIGNED)
    INTO v_retention_days
    FROM system_config
    WHERE config_key = 'data.retention.days';

    SET v_expire_date = DATE_SUB(NOW(), INTERVAL v_retention_days DAY);

    -- 标记删除过期的审核驳回记录
    UPDATE user_blessing
    SET is_deleted = 1
    WHERE audit_status = 2
      AND audit_time < v_expire_date
      AND is_deleted = 0;

    -- 删除过期的系统日志
    DELETE
    FROM system_log
    WHERE created_time < v_expire_date;

    -- 删除过期的限流记录
    DELETE
    FROM rate_limit_record
    WHERE updated_time < DATE_SUB(NOW(), INTERVAL 7 DAY);

END$$

DELIMITER ;

-- ==========================================
-- 数据库初始化完成
-- ==========================================
