CREATE
    DATABASE IF NOT EXISTS "tcp" DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE
    "tcp";
-- --------------------------------------------------------

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `user_id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`    varchar(255)     NOT NULL,
    `password` varchar(255)     NOT NULL,
    PRIMARY KEY (`user_id`)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_unicode_ci;
