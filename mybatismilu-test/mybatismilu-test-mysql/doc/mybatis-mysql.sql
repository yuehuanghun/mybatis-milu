-- --------------------------------------------------------
-- 服务器版本:                        8.0.16 - MySQL Community Server - GPL
-- 服务器OS:                        Win64
-- HeidiSQL 版本:                  10.2.0.5599
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `class` (
  `id` bigint NOT NULL COMMENT 'ID',
  `add_time` datetime DEFAULT NULL COMMENT '增加时间',
  `name` varchar(255) DEFAULT NULL COMMENT '名字',
  `data` varchar(1000) DEFAULT NULL COMMENT '数据',
  `is_deleted` char(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `class` (`id`, `add_time`, `name`) VALUES
	(1, '2017-06-04 16:14:44', '一年级'),
	(2, '2017-06-04 16:14:59', '二年级');

CREATE TABLE IF NOT EXISTS `class_teacher_rel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `add_time` datetime DEFAULT NULL,
  `class_id` bigint(20) DEFAULT NULL,
  `teacher_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK73d8r502wak4ktp8nxubh5yfd` (`teacher_id`),
  KEY `FK7p9cbodmadnrj9pggo0lyjodi` (`class_id`),
  CONSTRAINT `FK73d8r502wak4ktp8nxubh5yfd` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`id`),
  CONSTRAINT `FK7p9cbodmadnrj9pggo0lyjodi` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

INSERT INTO `class_teacher_rel` (`id`, `add_time`, `class_id`, `teacher_id`) VALUES
	(1, '2017-08-21 09:04:10', 1, 1),
	(2, '2017-08-21 16:46:25', 2, 1),
	(3, '2017-08-21 16:50:13', 1, 2);

CREATE TABLE IF NOT EXISTS `menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `pid` bigint NOT NULL DEFAULT '0' COMMENT '父ID',
  `name` varchar(50) NOT NULL DEFAULT '' COMMENT '菜单名称',
  `add_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  `delete_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COMMENT='菜单';

INSERT INTO `menu` (`id`, `pid`, `name`, `add_time`, `update_time`) VALUES
	(1, 0, '一级1', '2021-04-02 22:21:11', '2021-04-02 22:21:13', 0, NULL),
	(2, 0, '一级2', '2021-04-02 22:21:21', '2022-04-30 12:47:47', 1, '2022-04-30 12:47:47'),
	(3, 1, '二级1', '2021-04-02 22:21:30', '2021-04-02 22:21:31', 0, NULL),
	(4, 1, '二级2', '2021-04-02 22:21:41', '2021-04-02 22:21:49', 0, NULL),
	(5, 2, '二级3', '2021-04-02 22:22:02', '2021-04-02 22:22:02', 1, '2022-04-30 11:48:40'),
	(6, 5, '三级1', '2021-04-02 22:22:16', '2021-04-02 22:22:17', 0, NULL);

CREATE TABLE IF NOT EXISTS `sequence` (
  `id` int(10) unsigned NOT NULL COMMENT 'id',
  `current_seq` bigint(20) unsigned NOT NULL,
  `seq_name` varchar(50) DEFAULT '',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='序列';

INSERT INTO `sequence` (`id`, `current_seq`, `seq_name`, `update_time`) VALUES
	(1, 500, '默认序列', '2021-03-14 15:26:09');

CREATE TABLE IF NOT EXISTS `student` (
  `id` bigint NOT NULL,
  `add_time` datetime DEFAULT NULL,
  `age` int NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `class_id` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `is_deleted` tinyint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKdwhkib64u47wc4yo4hk0cub90` (`class_id`),
  CONSTRAINT `FKdwhkib64u47wc4yo4hk0cub90` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `student` (`id`, `add_time`, `age`, `name`, `class_id`, `update_time`) VALUES
	(1, '2021-03-14 10:35:47', 7, '蓝天', 1, NULL),
	(2, '2017-06-04 16:15:11', 9, '王小一', 2, NULL),
	(3, '2017-06-08 11:36:48', 8, '张三', 1, NULL),
	(4, '2017-06-08 14:40:16', 8, '李四', 1, NULL),
	(5, '2017-06-08 14:40:43', 9, '王五', 2, NULL);

CREATE TABLE IF NOT EXISTS `student_profile` (
  `id` bigint(20) NOT NULL,
  `student_id` bigint(20) DEFAULT NULL,
  `father_name` varchar(50) DEFAULT '' COMMENT '父亲姓名',
  `mother_name` varchar(50) DEFAULT '' COMMENT '母亲姓名',
  `father_age` int(11) DEFAULT NULL COMMENT '父新年龄',
  `mother_age` int(11) DEFAULT NULL COMMENT '母亲年龄',
  `add_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='学生画像';

INSERT INTO `student_profile` (`id`, `student_id`, `father_name`, `mother_name`, `father_age`, `mother_age`, `add_time`, `update_time`) VALUES
	(1, 1, '蓝标', '张兰', 33, 28, '2021-03-14 10:38:25', '2021-03-14 19:14:09'),
	(2, 2, '王海飞', '梁晓菊', 32, 29, '2021-03-14 10:39:34', '2021-03-14 19:14:11');

CREATE TABLE `teacher` (
`id` bigint NOT NULL AUTO_INCREMENT,
  `add_time` datetime DEFAULT NULL,
  `age` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `revision` int DEFAULT '0',
  `is_deleted` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

INSERT INTO `teacher` (`id`, `add_time`, `age`, `name`,`revision`) VALUES
	(1, '2017-08-21 09:03:28', '30', '黄老师',0),
	(2, '2017-08-21 16:49:56', '28', '何老师',0);

	
CREATE TABLE IF NOT EXISTS `attachment` (
  `id` bigint NOT NULL,
  `file_name` varchar(50) NOT NULL DEFAULT '',
  `file_path` varchar(500) NOT NULL DEFAULT '',
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='附件表';

INSERT INTO `attachment` (`id`, `file_name`, `file_path`, `create_time`) VALUES
	(1, '一年级数学上册', '/dd/dd.pdf', '2022-06-07 22:50:48'),
	(2, '一年级语文上册', '/ee/ee.pdf', '2022-06-07 22:51:27'),
	(3, '二年级数学上册', '/ff/ff.pdf', '2022-06-07 23:08:39'),
	(4, '二年级思想与政治上册', '/gg/gg.pdf', '2022-06-07 23:09:17');

CREATE TABLE IF NOT EXISTS `attachment_ref` (
  `id` bigint NOT NULL,
  `attachment_id` bigint NOT NULL,
  `attachment_type` varchar(10) NOT NULL DEFAULT '',
  `ref_id` bigint NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='附件引用';

INSERT INTO `attachment_ref` (`id`, `attachment_id`, `attachment_type`, `ref_id`, `create_time`) VALUES
	(1, 1, '1', 1, '2022-06-07 22:52:17'),
	(2, 2, '1', 1, '2022-06-07 22:52:29'),
	(3, 3, '1', 2, '2022-06-07 23:10:05'),
	(4, 4, '1', 2, '2022-06-07 23:10:14');

CREATE TABLE IF NOT EXISTS `teaching_plan` (
  `id` bigint NOT NULL,
  `plan_name` varchar(50) NOT NULL DEFAULT '',
  `plan_descp` varchar(100) DEFAULT '',
  `attachment_type` varchar(10) NOT NULL DEFAULT '',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='教案';

INSERT INTO `teaching_plan` (`id`, `plan_name`, `plan_descp`, `attachment_type`, `create_time`) VALUES
	(1, '一年级教案集', '这是描述', '1', '2022-06-07 22:51:59'),
	(2, '二年级教案集', '这是描述', '1', '2022-06-07 23:09:46');

