drop database if exists huava;
drop user if exists 'huava'@'%';
create database huava default character set utf8mb4 collate utf8mb4_general_ci;
use huava;
create user 'huava'@'%' identified by 'eFS0H6_0_pkVm__o';
grant all privileges on huava.* to 'huava'@'%';
flush privileges;
CREATE TABLE `sys_tenant` (
  `id` bigint NOT NULL COMMENT '主键',
  `code` char(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '000000' COMMENT '租户代码，6 位字符',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '租户名称',
  `domain` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '域名地址',
  `contact_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系人',
  `contact_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系电话',
  `expire_time` datetime NOT NULL DEFAULT '2100-01-01 00:00:00' COMMENT '过期时间',
  `max_user` int NOT NULL DEFAULT '-1' COMMENT '最大用户数量，<=0 表示不限',
  `remark` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `delete_flag` bigint NOT NULL DEFAULT '0' COMMENT '删除标记，0 代表未删除，= id 代表已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique-code-delete_flag` (`code`,`delete_flag`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='租户';
INSERT INTO `huava`.`sys_tenant` (`id`, `code`, `name`, `domain`, `contact_name`, `contact_number`, `expire_time`, `max_user`, `remark`, `delete_flag`) VALUES (1, '000000', '默认租户', NULL, NULL, NULL, '2100-01-01', -1, NULL, 0);
INSERT INTO `huava`.`sys_tenant` (`id`, `code`, `name`, `domain`, `contact_name`, `contact_number`, `expire_time`, `max_user`, `remark`, `delete_flag`) VALUES (2, '000002', '2', NULL, NULL, NULL, '2100-01-01', -1, NULL, 0);
