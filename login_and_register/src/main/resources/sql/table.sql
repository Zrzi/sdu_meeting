CREATE TABLE `user`(
    `user_id` BIGINT AUTO_INCREMENT,
    `username` VARCHAR(32) UNIQUE NOT NULL,
    `email` VARCHAR(64) UNIQUE NOT NULL,
    `password` VARCHAR(32) NOT NULL,
    `profile` VARCHAR(32),
    `code` VARCHAR(256),
    `status` INTEGER,
    PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `user` (`username`, `email`, `password`, `profile`)
VALUES ('user', '201900301042@mail.sdu.edu.cn', '827ccb0eea8a706c4c34a16891f84e7b', 'default.png');

CREATE TABLE `role`(
    `role_id` BIGINT AUTO_INCREMENT,
    `authority` VARCHAR(32) UNIQUE NOT NULL,
    PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `role` (`authority`)
VALUES ('user');

CREATE TABLE `user_role`(
    `user_id` BIGINT NOT NULL,
    `role_id` BIGINT NOT NULL,
    PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `user_role` (`user_id`, `role_id`)
VALUES (1, 1);

UPDATE `user`
SET `email` = '201900301041@mail.sdu.edu.cn'
WHERE `user_id` = 1;

UPDATE `user`
SET `email` = '201900301042@mail.sdu.edu.cn'
WHERE `user_id` = 2;

UPDATE `user`
SET `email` = '201900301043@mail.sdu.edu.cn'
WHERE `user_id` = 3;

ALTER TABLE `user` MODIFY COLUMN `email` VARCHAR(64) UNIQUE NOT NULL;

ALTER TABLE `user` ADD COLUMN `code` VARCHAR(256);

ALTER TABLE `user` ADD COLUMN `status` INTEGER;