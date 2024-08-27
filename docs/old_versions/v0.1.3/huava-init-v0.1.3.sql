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
-- Table structure for sys_refresh_token
-- ----------------------------
DROP TABLE IF EXISTS `sys_refresh_token`;
CREATE TABLE `sys_refresh_token`  (
  `id` bigint UNSIGNED NOT NULL,
  `refresh_token` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `sys_user_id` bigint NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `delete_info` bigint NOT NULL COMMENT '0: undeleted; other: deleted time',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_refresh_token`(`refresh_token` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_refresh_token
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, 'ROLE_ADMIN');
INSERT INTO `sys_role` VALUES (2, 'ROLE_USER');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `login_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录账号',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户昵称',
  `user_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '00' COMMENT '用户类型（00系统用户 01注册用户）',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phone_number` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '手机号码',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '头像路径',
  `password` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '密码',
  `salt` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '盐加密',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `pwd_update_date` datetime NULL DEFAULT NULL COMMENT '密码最后更新时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 102 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '后台用户' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 103, 'admin', '若依', '00', 'ry@163.com', '15888888888', '1', '', '$2a$10$lfPbkm1vP1C0dxqmN6Ze9.pBiSSFEXR3zPByrkEto.hPNE8K35yHW', '111111', '0', '0', '127.0.0.1', NULL, NULL, 'admin', '2024-08-05 21:38:19', '', NULL, '管理员');
INSERT INTO `sys_user` VALUES (2, 105, 'ry', '若依222', '00', 'ry222@qq.com', '15666666666', '1', '', '8e6d98b90472783cc73c17047ddccf36', '222222', '0', '0', '127.0.0.1', NULL, NULL, 'admin', '2024-08-05 21:38:19', '', NULL, '测试员');
INSERT INTO `sys_user` VALUES (100, 103, 'john', 'John', '00', 'john@gmail.com', '', '0', '', '$2a$12$HeVk0EIlGIWq5hNKf2Ziq.9iTs2cAHUFaZG.8s7ahbPto.0gApDti', '', '0', '0', '', NULL, NULL, '', NULL, '', NULL, NULL);
INSERT INTO `sys_user` VALUES (101, 105, 'lakshitha', 'Lakshitha', '00', 'Lakshitha@gmail.com', '', '0', '', '$2a$12$UD73VrZpFcVI1M.Mrfw/bOSNEG.FgrFpCKDcpDRHzKA1awE4nguqG', '', '0', '0', '', NULL, NULL, '', NULL, '', NULL, NULL);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE,
  INDEX `FKj6m8fwv7oqv74fcehir1a9ffy`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (100, 1);
INSERT INTO `sys_user_role` VALUES (101, 2);

SET FOREIGN_KEY_CHECKS = 1;
