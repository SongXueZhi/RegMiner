CREATE TABLE `regression` (
  `regression_id` int(10) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `regression_status` int(11) DEFAULT '0',
  `project_name` varchar(255) CHARACTER SET latin1 NOT NULL,
  `bug_id` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
  `bfc` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
  `buggy` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
  `bic` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
  `work` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
  `testcase` text CHARACTER SET latin1,
  PRIMARY KEY (`regression_id`),
  UNIQUE KEY `weiyi` (`bug_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;