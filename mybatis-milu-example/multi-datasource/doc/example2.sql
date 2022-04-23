
CREATE DATABASE IF NOT EXISTS `example2` 
USE `example2`;

CREATE TABLE IF NOT EXISTS `example_two` (
  `id` bigint NOT NULL,
  `code` varchar(50)  NOT NULL DEFAULT '',
  `name` varchar(50)  NOT NULL DEFAULT '',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) COMMENT='样例表2';
