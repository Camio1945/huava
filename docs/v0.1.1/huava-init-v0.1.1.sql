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
-- Table structure for oauth2_authorization
-- ----------------------------
DROP TABLE IF EXISTS `oauth2_authorization`;
CREATE TABLE `oauth2_authorization`
(
    `id`                            varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL,
    `registered_client_id`          varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL,
    `principal_name`                varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL,
    `authorization_grant_type`      varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL,
    `authorized_scopes`             varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `attributes`                    blob                                                           NULL,
    `state`                         varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL,
    `authorization_code_value`      blob                                                           NULL,
    `authorization_code_issued_at`  timestamp                                                      NULL DEFAULT NULL,
    `authorization_code_expires_at` timestamp                                                      NULL DEFAULT NULL,
    `authorization_code_metadata`   blob                                                           NULL,
    `access_token_value`            blob                                                           NULL,
    `access_token_issued_at`        timestamp                                                      NULL DEFAULT NULL,
    `access_token_expires_at`       timestamp                                                      NULL DEFAULT NULL,
    `access_token_metadata`         blob                                                           NULL,
    `access_token_type`             varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL,
    `access_token_scopes`           varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `oidc_id_token_value`           blob                                                           NULL,
    `oidc_id_token_issued_at`       timestamp                                                      NULL DEFAULT NULL,
    `oidc_id_token_expires_at`      timestamp                                                      NULL DEFAULT NULL,
    `oidc_id_token_metadata`        blob                                                           NULL,
    `refresh_token_value`           blob                                                           NULL,
    `refresh_token_issued_at`       timestamp                                                      NULL DEFAULT NULL,
    `refresh_token_expires_at`      timestamp                                                      NULL DEFAULT NULL,
    `refresh_token_metadata`        blob                                                           NULL,
    `user_code_value`               blob                                                           NULL,
    `user_code_issued_at`           timestamp                                                      NULL DEFAULT NULL,
    `user_code_expires_at`          timestamp                                                      NULL DEFAULT NULL,
    `user_code_metadata`            blob                                                           NULL,
    `device_code_value`             blob                                                           NULL,
    `device_code_issued_at`         timestamp                                                      NULL DEFAULT NULL,
    `device_code_expires_at`        timestamp                                                      NULL DEFAULT NULL,
    `device_code_metadata`          blob                                                           NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oauth2_authorization
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`
(
    `id`   bigint                                                        NOT NULL AUTO_INCREMENT,
    `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role`
VALUES (1, 'ROLE_ADMIN');
INSERT INTO `sys_role`
VALUES (2, 'ROLE_USER');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `user_id`         bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `dept_id`         bigint                                                        NULL DEFAULT NULL COMMENT '部门ID',
    `login_name`      varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '登录账号',
    `user_name`       varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT '' COMMENT '用户昵称',
    `user_type`       varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT '00' COMMENT '用户类型（00系统用户 01注册用户）',
    `email`           varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT '' COMMENT '用户邮箱',
    `phone_number`    varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT '' COMMENT '手机号码',
    `sex`             char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      NULL DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
    `avatar`          varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '头像路径',
    `password`        varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT '' COMMENT '密码',
    `salt`            varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT '' COMMENT '盐加密',
    `status`          char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      NULL DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
    `del_flag`        char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
    `login_ip`        varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '最后登录IP',
    `login_date`      datetime                                                      NULL DEFAULT NULL COMMENT '最后登录时间',
    `pwd_update_date` datetime                                                      NULL DEFAULT NULL COMMENT '密码最后更新时间',
    `create_by`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT '' COMMENT '创建者',
    `create_time`     datetime                                                      NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT '' COMMENT '更新者',
    `update_time`     datetime                                                      NULL DEFAULT NULL COMMENT '更新时间',
    `remark`          varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1822468986445860867
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '后台用户'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user`
VALUES (1, 103, 'admin', '若依', '00', 'ry@163.com', '15888888888', '1', '',
        '$2a$10$lfPbkm1vP1C0dxqmN6Ze9.pBiSSFEXR3zPByrkEto.hPNE8K35yHW', '111111', '0', '0', '127.0.0.1', NULL, NULL,
        'admin', '2024-08-05 21:38:19', '', NULL, '管理员');
INSERT INTO `sys_user`
VALUES (2, 105, 'ry', '若依222', '00', 'ry222@qq.com', '15666666666', '1', '', '8e6d98b90472783cc73c17047ddccf36',
        '222222', '0', '0', '127.0.0.1', NULL, NULL, 'admin', '2024-08-05 21:38:19', '', NULL, '测试员222');

SET FOREIGN_KEY_CHECKS = 1;
