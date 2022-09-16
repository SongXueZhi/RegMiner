CREATE TABLE `account` (
  `uuid` varchar(36) CHARACTER SET utf8mb4 NOT NULL COMMENT '用户唯一id',
  `account_name` varchar(128) CHARACTER SET utf8mb4 NOT NULL COMMENT '登录时用户名',
  `password` varchar(32) CHARACTER SET utf8mb4 NOT NULL COMMENT '用户密码，加密后的',
  `email` varchar(64) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '用户邮箱',
  `account_right` tinyint(4) DEFAULT '1' COMMENT '用户权限（0、1）：超级管理员（ADMIN）为0、其他为1',
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `idx_account_name` (`account_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;