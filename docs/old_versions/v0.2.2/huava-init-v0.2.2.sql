drop database if exists huava;
create database huava default character set utf8mb4 collate utf8mb4_general_ci;
use huava;
create user if not exists 'huava'@'%' identified by 'eFS0H6_0_pkVm__o';
grant all privileges on huava.* to 'huava'@'%';
flush privileges;


SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for common_attachment
-- ----------------------------
DROP TABLE IF EXISTS `common_attachment`;
CREATE TABLE `common_attachment`
(
    `id`            bigint UNSIGNED                                               NOT NULL COMMENT '主键',
    `url`           varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '地址',
    `size`          bigint                                                        NOT NULL COMMENT '文件大小（字节）',
    `human_size`    varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '文件大小（适合人类阅读习惯）',
    `original_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '原文件名称',
    `create_time`   datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `idx_url` (`url` ASC) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '附件'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of common_attachment
-- ----------------------------

-- ----------------------------
-- Table structure for sys_perm
-- ----------------------------
DROP TABLE IF EXISTS `sys_perm`;
CREATE TABLE `sys_perm`
(
    `id`          bigint UNSIGNED                                               NOT NULL AUTO_INCREMENT COMMENT '主键',
    `pid`         bigint UNSIGNED                                               NOT NULL DEFAULT 0 COMMENT '上级菜单',
    `type`        char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      NOT NULL DEFAULT '' COMMENT '权限类型: D=目录(Directory)，M=菜单(Menu)，E=元素(Element)',
    `name`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '菜单名称',
    `icon`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '菜单图标',
    `sort`        smallint UNSIGNED                                             NOT NULL DEFAULT 0 COMMENT '菜单排序',
    `uri`         varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '接口URI',
    `paths`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '路由地址',
    `component`   varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '前端组件',
    `selected`    varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '选中路径',
    `params`      varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '路由参数',
    `is_cache`    tinyint UNSIGNED                                              NOT NULL DEFAULT 0 COMMENT '是否缓存',
    `is_show`     tinyint UNSIGNED                                              NOT NULL DEFAULT 1 COMMENT '是否显示',
    `is_enabled`  tinyint UNSIGNED                                              NOT NULL DEFAULT 0 COMMENT '是否启用',
    `create_time` datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `delete_info` bigint                                                        NOT NULL COMMENT '删除信息：0-未删除，其他值-删除时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1830534491038732290
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '权限'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_perm
-- ----------------------------
INSERT INTO `sys_perm`
VALUES (4, 0, 'D', '用户角色权限', 'el-icon-Lock', 100, '', 'sys', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-29 09:50:01', 0);
INSERT INTO `sys_perm`
VALUES (5, 0, 'M', '工作台', 'el-icon-Monitor', 0, 'workbench/index', 'workbench', 'workbench/index', '', '', 0, 1, 0,
        '2024-08-28 10:20:00', '2024-08-29 09:47:16', 0);
INSERT INTO `sys_perm`
VALUES (6, 4, 'M', '权限', 'el-icon-Operation', 100, '/sys/perm/page', 'perm/permPage', 'sys/perm/permPage', '', '', 1,
        1, 1, '2024-08-28 10:20:00', '2024-08-30 00:11:26', 0);
INSERT INTO `sys_perm`
VALUES (7, 4, 'M', '用户', 'local-icon-shouyiren', 80, '/sys/user/page', 'user/userPage', 'sys/user/userPage', '', '',
        1, 1, 1, '2024-08-28 10:20:00', '2024-08-30 00:03:59', 0);
INSERT INTO `sys_perm`
VALUES (8, 4, 'M', '角色', 'el-icon-Female', 90, '/sys/role/page', 'role/rolePage', 'sys/role/rolePage', '', '', 1, 1,
        1, '2024-08-28 10:20:00', '2024-08-30 00:05:05', 0);
INSERT INTO `sys_perm`
VALUES (12, 8, 'E', '新增', '', 1, '/sys/role/create', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-30 00:05:18', 0);
INSERT INTO `sys_perm`
VALUES (14, 8, 'E', '编辑', '', 3, '/sys/role/update', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-30 00:07:15', 0);
INSERT INTO `sys_perm`
VALUES (15, 8, 'E', '删除', '', 2, '/sys/role/delete', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-30 00:07:08', 0);
INSERT INTO `sys_perm`
VALUES (16, 6, 'E', '新增', '', 1, '/sys/perm/create', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-30 00:11:38', 0);
INSERT INTO `sys_perm`
VALUES (17, 6, 'E', '编辑', '', 3, '/sys/perm/update', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-30 00:12:01', 0);
INSERT INTO `sys_perm`
VALUES (18, 6, 'E', '删除', '', 2, '/sys/perm/delete', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-30 00:11:48', 0);
INSERT INTO `sys_perm`
VALUES (19, 7, 'E', '新增', '', 1, '/sys/user/create', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-30 00:03:01', 0);
INSERT INTO `sys_perm`
VALUES (20, 7, 'E', '编辑', '', 1, '/sys/user/update', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-30 00:02:51', 0);
INSERT INTO `sys_perm`
VALUES (21, 7, 'E', '删除', '', 1, '/sys/user/delete', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-30 00:02:41', 0);
INSERT INTO `sys_perm`
VALUES (23, 28, 'D', '开发工具', 'el-icon-EditPen', 40, '', 'dev_tools', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (24, 23, 'M', '代码生成器', 'el-icon-DocumentAdd', 1, 'tools.generator/generateTable', 'code',
        'dev_tools/code/index', '', '', 0, 1, 1, '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (25, 0, 'D', '组织管理', 'el-icon-OfficeBuilding', 400, '', 'organization', '', '', '', 0, 1, 0,
        '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (26, 25, 'M', '部门管理', 'el-icon-Coordinate', 100, 'dept.dept/lists', 'department',
        'organization/department/index', '', '', 1, 1, 1, '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (27, 25, 'M', '岗位管理', 'el-icon-PriceTag', 90, 'dept.jobs/lists', 'post', 'organization/post/index', '', '',
        0, 1, 1, '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (28, 0, 'D', '系统设置', 'el-icon-Setting', 300, '', 'setting', '', '', '', 0, 1, 0, '2024-08-28 10:20:00',
        '2024-08-29 09:48:57', 0);
INSERT INTO `sys_perm`
VALUES (29, 28, 'D', '网站设置', 'el-icon-Basketball', 100, '', 'website', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (30, 29, 'M', '网站信息', '', 1, 'setting.web.web_setting/getWebsite', 'information',
        'setting/website/information', '', '', 0, 1, 1, '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (31, 29, 'M', '网站备案', '', 1, 'setting.web.web_setting/getCopyright', 'filing', 'setting/website/filing', '',
        '', 0, 1, 1, '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (32, 29, 'M', '政策协议', '', 1, 'setting.web.web_setting/getAgreement', 'protocol', 'setting/website/protocol',
        '', '', 0, 1, 1, '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (33, 28, 'M', '存储设置', 'el-icon-FolderOpened', 70, 'setting.storage/lists', 'storage',
        'setting/storage/index', '', '', 0, 1, 1, '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (34, 23, 'M', '字典管理', 'el-icon-Box', 1, 'setting.dict.dict_type/lists', 'dict', 'setting/dict/type/index',
        '', '', 0, 1, 1, '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (35, 28, 'D', '系统维护', 'el-icon-SetUp', 50, '', 'system', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (36, 35, 'M', '系统日志', '', 90, 'setting.system.log/lists', 'journal', 'setting/system/journal', '', '', 0, 1,
        1, '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (37, 35, 'M', '系统缓存', '', 80, '', 'cache', 'setting/system/cache', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (38, 35, 'M', '系统环境', '', 70, 'setting.system.system/info', 'environment', 'setting/system/environment', '',
        '', 0, 1, 1, '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (39, 24, 'E', '导入数据表', '', 1, 'tools.generator/selectTable', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (40, 24, 'E', '代码生成', '', 1, 'tools.generator/generate', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (41, 23, 'M', '编辑数据表', '', 1, 'tools.generator/edit', 'code/edit', 'dev_tools/code/edit', '/dev_tools/code',
        '', 1, 0, 1, '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (42, 24, 'E', '同步表结构', '', 1, 'tools.generator/syncColumn', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (43, 24, 'E', '删除数据表', '', 1, 'tools.generator/delete', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (44, 24, 'E', '预览代码', '', 1, 'tools.generator/preview', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (45, 26, 'E', '新增', '', 1, 'dept.dept/add', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (46, 26, 'E', '编辑', '', 1, 'dept.dept/edit', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (47, 26, 'E', '删除', '', 1, 'dept.dept/delete', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (48, 27, 'E', '新增', '', 1, 'dept.jobs/add', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (49, 27, 'E', '编辑', '', 1, 'dept.jobs/edit', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (50, 27, 'E', '删除', '', 1, 'dept.jobs/delete', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (51, 30, 'E', '保存', '', 1, 'setting.web.web_setting/setWebsite', '', '', '', '', 0, 1, 1,
        '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (52, 31, 'E', '保存', '', 1, 'setting.web.web_setting/setCopyright', '', '', '', '', 0, 1, 1,
        '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (53, 32, 'E', '保存', '', 1, 'setting.web.web_setting/setAgreement', '', '', '', '', 0, 1, 1,
        '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (54, 33, 'E', '设置', '', 1, 'setting.storage/setup', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (55, 34, 'E', '新增', '', 1, 'setting.dict.dict_type/add', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (56, 34, 'E', '编辑', '', 1, 'setting.dict.dict_type/edit', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (57, 34, 'E', '删除', '', 1, 'setting.dict.dict_type/delete', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (58, 62, 'E', '新增', '', 1, 'setting.dict.dict_data/add', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (59, 62, 'E', '编辑', '', 1, 'setting.dict.dict_data/edit', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (60, 62, 'E', '删除', '', 1, 'setting.dict.dict_data/delete', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (61, 37, 'E', '清除系统缓存', '', 1, 'setting.system.cache/clear', '', '', '', '', 0, 1, 1,
        '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (62, 23, 'M', '字典数据管理', '', 1, 'setting.dict.dict_data/lists', 'dict/data', 'setting/dict/data/index',
        '/dev_tools/dict', '', 1, 0, 1, '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (66, 26, 'E', '详情', '', 0, 'dept.dept/detail', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (67, 27, 'E', '详情', '', 0, 'dept.jobs/detail', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (110, 28, 'M', '热门搜索', 'el-icon-Search', 60, 'setting.hot_search/getConfig', 'search',
        'setting/search/index', '', '', 0, 1, 1, '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (111, 110, 'E', '保存', '', 0, 'setting.hot_search/setConfig', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (112, 28, 'D', '用户设置', 'local-icon-keziyuyue', 90, '', 'user', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (113, 112, 'M', '用户设置', '', 0, 'setting.user.user/getConfig', 'setup', 'setting/user/setup', '', '', 0, 1, 1,
        '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (114, 113, 'E', '保存', '', 0, 'setting.user.user/setConfig', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (115, 112, 'M', '登录注册', '', 0, 'setting.user.user/getRegisterConfig', 'login_register',
        'setting/user/login_register', '', '', 0, 1, 1, '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (116, 115, 'E', '保存', '', 0, 'setting.user.user/setRegisterConfig', '', '', '', '', 0, 1, 1,
        '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (143, 35, 'M', '定时任务', '', 100, 'crontab.crontab/lists', 'scheduled_task',
        'setting/system/scheduled_task/index', '', '', 0, 1, 1, '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (144, 35, 'M', '定时任务添加/编辑', '', 0, 'crontab.crontab/add:edit', 'scheduled_task/edit',
        'setting/system/scheduled_task/edit', '/setting/system/scheduled_task', '', 0, 0, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (145, 143, 'E', '添加', '', 0, 'crontab.crontab/add', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (146, 143, 'E', '编辑', '', 0, 'crontab.crontab/edit', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (147, 143, 'E', '删除', '', 0, 'crontab.crontab/delete', '', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (148, 0, 'D', '模板示例', 'el-icon-SetUp', 1000, '', 'template', '', '', '', 0, 1, 0, '2024-08-28 10:20:00',
        '2024-08-29 09:47:02', 0);
INSERT INTO `sys_perm`
VALUES (149, 148, 'D', '组件示例', 'el-icon-Coin', 0, '', 'component', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (150, 149, 'M', '富文本', '', 90, '', 'rich_text', 'template/component/rich_text', '', '', 0, 1, 1,
        '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (151, 149, 'M', '上传文件', '', 80, '', 'upload', 'template/component/upload', '', '', 0, 1, 1,
        '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (152, 149, 'M', '图标', '', 100, '', 'icon', 'template/component/icon', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (153, 149, 'M', '文件选择器', '', 60, '', 'file', 'template/component/file', '', '', 0, 1, 1,
        '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (154, 149, 'M', '链接选择器', '', 50, '', 'link', 'template/component/link', '', '', 0, 1, 1,
        '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (155, 149, 'M', '超出自动打点', '', 40, '', 'overflow', 'template/component/overflow', '', '', 0, 1, 1,
        '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (156, 149, 'M', '悬浮input', '', 70, '', 'popover_input', 'template/component/popover_input', '', '', 0, 1, 1,
        '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (161, 28, 'D', '支付设置', 'local-icon-set_pay', 80, '', 'pay', '', '', '', 0, 1, 1, '2024-08-28 10:20:00',
        '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (162, 161, 'M', '支付方式', '', 0, 'setting.pay.pay_way/getPayWay', 'method', 'setting/pay/method/index', '', '',
        0, 1, 1, '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (163, 161, 'M', '支付配置', '', 0, 'setting.pay.pay_config/lists', 'config', 'setting/pay/config/index', '', '',
        0, 1, 1, '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (164, 162, 'E', '设置支付方式', '', 0, 'setting.pay.pay_way/setPayWay', '', '', '', '', 0, 1, 1,
        '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);
INSERT INTO `sys_perm`
VALUES (165, 163, 'E', '配置', '', 0, 'setting.pay.pay_config/setConfig', '', '', '', '', 0, 1, 1,
        '2024-08-28 10:20:00', '2024-08-27 23:40:45', 0);

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
    `sort`        int                                                           NOT NULL COMMENT '排序',
    `create_time` datetime                                                      NOT NULL COMMENT '创建时间',
    `update_time` datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `delete_info` bigint                                                        NOT NULL COMMENT '删除信息：0-未删除，其他值-删除时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_name-delete_info` (`name` ASC, `delete_info` ASC) USING BTREE,
    INDEX `idx_sort` (`sort` ASC) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '角色'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role`
VALUES (1, 'admin', '超级管理员', 1, '2024-08-17 09:11:59', '2024-08-22 17:04:43', 0);

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
    CONSTRAINT `fk_sys_role_perm-role_id` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `fk_sys_role_perm-perm_id` FOREIGN KEY (`perm_id`) REFERENCES `sys_perm` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
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
    `avatar`          varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT '' COMMENT '头像路径',
    `is_enabled`      tinyint(1)                                                    NOT NULL COMMENT '是否启用',
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
VALUES (1, 'admin', '$2a$10$KfJSqi4zVpOk0O3uRRtQnufyJWeUhicDmOfPfsqFgbpJ6x8/..i52', '超管', '15888888888', 'U',
        '/20240825/b242a9554556227ab242a9554556227a.jpg', 1, NULL, '127.0.0.1', NULL, '超级管理员',
        '2024-08-17 08:58:25', '2024-08-17 08:58:25', 0);

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
VALUES (1, 1, 1);

SET FOREIGN_KEY_CHECKS = 1;
