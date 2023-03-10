CREATE TABLE `sys_user` (
                            `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                            `username` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '登录账号',
                            `realname` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '真实姓名',
                            `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '密码',
                            `salt` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'md5密码盐',
                            `avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '头像',
                            `birthday` datetime NULL COMMENT '生日',
                            `phone` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '电话',
                            `status` tinyint(1) NULL COMMENT '性别(1-正常,2-冻结)',
                            `del_flag` tinyint(1) NULL COMMENT '删除状态(0-正常,1-已删除)',
                            `creator` varchar(32) CHARACTER SET utf8mb4 NOT NULL,
                            `modifier` varchar(32) CHARACTER SET utf8mb4 DEFAULT NULL,
                            `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            `gmt_modify` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            KEY `idx_su_del_flag`(`del_flag`) USING BTREE,
                            KEY `idx_su_status`(`status`) USING BTREE,
                            KEY `idx_su_username`(`username`) USING BTREE,
                            Unique KEY `uniq_sys_user_username`(`username`) USING BTREE,
                            Unique KEY `uniq_sys_user_phone`(`phone`) USING BTREE
) COMMENT='用户表';


CREATE TABLE `sys_log` (
                           `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                           `log_type` int(2) NULL COMMENT '日志类型（1登录日志，2操作日志）',
                           `log_content` varchar(1000) NULL COMMENT '日志内容',
                           `operate_type` int(2) NULL COMMENT '操作类型',
                           `userid` varchar(32) NULL COMMENT '操作用户账号',
                           `username` varchar(100)  NULL COMMENT '操作用户名称',
                           `ip` varchar(100)  NULL COMMENT 'IP',
                           `method` varchar(500)  NULL COMMENT '请求java方法',
                           `request_url` varchar(255)  NULL COMMENT '请求路径',
                           `request_param` longtext   NULL COMMENT '请求参数',
                           `request_type` varchar(10)   NULL COMMENT '请求类型',
                           `cost_time` bigint(20) NULL COMMENT '耗时',
                           `creator` varchar(32)  NOT NULL,
                           `modifier` varchar(32)  DEFAULT NULL,
                           `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           `gmt_modify` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                           `header` varchar(255) NULL COMMENT '请求header',
                           PRIMARY KEY (`id`),
                           KEY `idx_sl_operate_type`(`operate_type`) USING BTREE,
                           KEY `idx_sl_log_type`(`log_type`) USING BTREE,
                           KEY `idx_sl_userid`(`userid`) USING BTREE,
                           KEY `idx_sl_create_time`(`gmt_create`) USING BTREE
) COMMENT='系统日志表';


CREATE TABLE `chat_session` (
                                `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                `session_id` varchar(64) NOT NULL,
                                `start_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                `last_time` datetime NULL,
                                `ip` varchar(50) DEFAULT NULL COMMENT 'ip地址',
                                `user_id` int(11) DEFAULT NULL COMMENT '用户id',
                                `deleted` bit(1) NOT NULL  DEFAULT 0  COMMENT '是否标记删除',
                                `creator` varchar(32) NOT NULL,
                                `modifier` varchar(32)  DEFAULT NULL,
                                `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                `gmt_modify` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                PRIMARY KEY (`id`)
) COMMENT='会话信息表';


ALTER TABLE `chat_session`
    ADD UNIQUE INDEX `unique_session_id` (`session_id` ASC);
;


CREATE TABLE `chat_session_info` (
        `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
       `session_id` varchar(64) NOT NULL,
       `user_id` int(11) DEFAULT NULL COMMENT '用户id',
       `sequence` int(11) DEFAULT 0 COMMENT '顺序',
        `type` int(2) NOT NULL COMMENT '类型(1.问题2.回答)',
        `content` text NOT NULL COMMENT '会话内容',
        `creator` varchar(32)  NOT NULL,
        `modifier` varchar(32) DEFAULT NULL,
        `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
        `gmt_modify` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
        PRIMARY KEY (`id`)
) ;


ALTER TABLE `chat_session_info`
    ADD INDEX `idx_session_id` (`session_id` ASC, `sequence` ASC),
ADD INDEX `idx_user_id` (`user_id` ASC);
;
