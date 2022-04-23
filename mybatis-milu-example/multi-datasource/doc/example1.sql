
CREATE DATABASE IF NOT EXISTS `example1` 
USE `example1`;

CREATE TABLE IF NOT EXISTS `example_one` (
  `id` bigint NOT NULL,
  `name` varchar(50) NOT NULL DEFAULT '',
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) COMMENT='样例1';
