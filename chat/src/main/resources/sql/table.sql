CREATE TABLE `message` (
    `message_id` BIGINT AUTO_INCREMENT,
    `from_id` BIGINT NOT NULL,
    `to_id` BIGINT NOT NULL,
    `message` VARCHAR(256),
    `date` BIGINT,
    `status` INTEGER,
    PRIMARY KEY (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `friend` (
    `user_id` BIGINT NOT NULL,
    `friend_id` BIGINT NOT NULL,
    PRIMARY KEY (`user_id`, `friend_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SELECT *
FROM `message`
WHERE `from_id` = 10 AND `to_id` = 11
    AND `status` in (0, 1)
ORDER BY `message_id` DESC;

SELECT *
FROM `message`
WHERE `from_id` = 10 AND `to_id` = 11
  AND `status` in (0, 1)
UNION
SELECT *
FROM `message`
WHERE `from_id` = 11 AND `to_id` = 10
  AND `status` in (0, 1)
ORDER BY `message_id` DESC;