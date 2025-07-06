CREATE DATABASE IF NOT EXISTS example_user;

USE example_user;

DROP TABLE IF EXISTS user;

CREATE TABLE user
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    email    VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name     VARCHAR(100) NOT NULL
);

INSERT INTO user (email, password, name)
VALUES ('example@example.com', '(ENCRYPTED)', '테스트 유저');
