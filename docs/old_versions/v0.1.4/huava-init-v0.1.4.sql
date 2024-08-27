drop database if exists huava;
drop user if exists 'huava'@'%';
create database huava default character set utf8mb4 collate utf8mb4_general_ci;
use huava;
create user 'huava'@'%' identified by 'eFS0H6_0_pkVm__o';
grant all privileges on huava.* to 'huava'@'%';
flush privileges;


SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_perm
-- ----------------------------
DROP TABLE IF EXISTS `sys_perm`;
CREATE TABLE `sys_perm`
(
    `id`          bigint UNSIGNED                                               NOT NULL COMMENT '主键',
    `type`        char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      NOT NULL COMMENT '类型：M-菜单（用户是否能看到该页面），E-元素（用户是否能看到页面上的某个元素）',
    `name`        varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL COMMENT '名称',
    `parent_id`   bigint                                                        NOT NULL COMMENT '父菜单ID：0-一级菜单，其他值-父菜单ID',
    `order_num`   int                                                           NOT NULL COMMENT '显示顺序：由低到高',
    `url`         varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求地址',
    `target`      char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      NULL     DEFAULT NULL COMMENT '打开方式：C-当前窗口（Current），B-新窗口（Blank）',
    `is_enabled`  tinyint(1)                                                    NOT NULL DEFAULT 0 COMMENT '是否启用',
    `icon`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL COMMENT '图标',
    `mark`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '标识：如 sys:user:page',
    `create_time` datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `delete_info` bigint                                                        NOT NULL COMMENT '删除信息：0-未删除，其他值-删除时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_parent_id` (`parent_id` ASC) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '权限'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_perm
-- ----------------------------

-- ----------------------------
-- Table structure for sys_refresh_token
-- ----------------------------
DROP TABLE IF EXISTS `sys_refresh_token`;
CREATE TABLE `sys_refresh_token`
(
    `id`            bigint UNSIGNED                                               NOT NULL COMMENT '主键',
    `refresh_token` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '刷新令牌',
    `sys_user_id`   bigint UNSIGNED                                               NOT NULL COMMENT '用户ID',
    `create_time`   datetime                                                      NOT NULL COMMENT '创建时间',
    `update_time`   datetime                                                      NOT NULL COMMENT '更新时间',
    `delete_info`   bigint                                                        NOT NULL COMMENT '删除信息：0-未删除，其他值-删除时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_refresh_token` (`refresh_token` ASC) USING BTREE,
    INDEX `fk_sys_refresh_token-sys_user_id` (`sys_user_id` ASC) USING BTREE,
    CONSTRAINT `fk_sys_refresh_token-sys_user_id` FOREIGN KEY (`sys_user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '刷新令牌'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_refresh_token
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`
(
    `id`          bigint UNSIGNED                                               NOT NULL COMMENT '主键',
    `name`        varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '名称',
    `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '描述',
    `create_time` datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `delete_info` bigint                                                        NOT NULL COMMENT '删除信息：0-未删除，其他值-删除时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '角色'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role`
VALUES (1, 'ROLE_ADMIN', NULL, '2024-08-17 09:11:59', '2024-08-17 09:11:59', 0);
INSERT INTO `sys_role`
VALUES (2, 'ROLE_USER', NULL, '2024-08-17 09:11:59', '2024-08-17 09:11:59', 0);

-- ----------------------------
-- Table structure for sys_role_perm
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_perm`;
CREATE TABLE `sys_role_perm`
(
    `id`      bigint UNSIGNED NOT NULL COMMENT '主键',
    `role_id` bigint UNSIGNED NOT NULL COMMENT '角色ID',
    `perm_id` bigint UNSIGNED NOT NULL COMMENT '权限ID',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_sys_role_perm-role_id` (`role_id` ASC) USING BTREE,
    INDEX `idx_sys_role_perm-perm_id` (`perm_id` ASC) USING BTREE,
    CONSTRAINT `fk_sys_role_perm-perm_id` FOREIGN KEY (`perm_id`) REFERENCES `sys_perm` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `fk_sys_role_perm-role_id` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '角色拥有的权限'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_perm
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `id`              bigint UNSIGNED                                               NOT NULL COMMENT '主键',
    `username`        varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '用户名',
    `password`        varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '密码',
    `real_name`       varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL COMMENT '真实姓名',
    `phone_number`    varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT '' COMMENT '手机号',
    `gender`          char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      NULL     DEFAULT '0' COMMENT '用户性别：M-男，F-女，U-未知',
    `avatar`          varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT '' COMMENT '头像路径',
    `is_enabled`      tinyint(1)                                                    NOT NULL DEFAULT 0 COMMENT '是否启用',
    `disabled_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '禁用原因',
    `last_login_ip`   varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT '' COMMENT '最后登录IP',
    `last_login_date` datetime                                                      NULL     DEFAULT NULL COMMENT '最后登录时间',
    `remark`          varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '备注',
    `create_time`     datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `delete_info`     bigint                                                        NOT NULL COMMENT '删除信息：0-未删除，其他值-删除时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_username-delete_info` (`username` ASC, `delete_info` ASC) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '用户'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user`
VALUES (1, '若依', '$2a$10$lfPbkm1vP1C0dxqmN6Ze9.pBiSSFEXR3zPByrkEto.hPNE8K35yHW', NULL, '15888888888', '1', '', 0,
        NULL, '127.0.0.1', NULL, '管理员', '2024-08-17 08:58:25', '2024-08-17 08:58:25', 0);
INSERT INTO `sys_user`
VALUES (2, '若依222', '8e6d98b90472783cc73c17047ddccf36', NULL, '15666666666', '1', '', 0, NULL, '127.0.0.1', NULL,
        '测试员', '2024-08-17 08:58:25', '2024-08-17 08:58:25', 0);
INSERT INTO `sys_user`
VALUES (100, 'John', '$2a$12$HeVk0EIlGIWq5hNKf2Ziq.9iTs2cAHUFaZG.8s7ahbPto.0gApDti', NULL, '', '0', '', 0, NULL, '',
        NULL, NULL, '2024-08-17 08:58:25', '2024-08-17 08:58:25', 0);
INSERT INTO `sys_user`
VALUES (101, 'Lakshitha', '$2a$12$UD73VrZpFcVI1M.Mrfw/bOSNEG.FgrFpCKDcpDRHzKA1awE4nguqG', NULL, '', '0', '', 0, NULL,
        '', NULL, NULL, '2024-08-17 08:58:25', '2024-08-17 08:58:25', 0);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`
(
    `id`      bigint UNSIGNED NOT NULL COMMENT '主键',
    `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
    `role_id` bigint UNSIGNED NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_sys_user_role-role_id` (`role_id` ASC) USING BTREE,
    INDEX `idx_sys_user_role-user_id` (`user_id` ASC) USING BTREE,
    CONSTRAINT `fk_user_role-role_id` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `fk_user_role-user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '用户所属的角色'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role`
VALUES (1, 100, 1);
INSERT INTO `sys_user_role`
VALUES (2, 101, 2);

SET FOREIGN_KEY_CHECKS = 1;
