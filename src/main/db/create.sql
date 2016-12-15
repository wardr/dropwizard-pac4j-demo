-- SQL to create tables, etc used by demo app
--
-- To set-up database and user:
--
-- drop database if exists demo;
-- create database demo;
-- CREATE USER 'demo_user'@'localhost' IDENTIFIED BY 'demopassword';
-- GRANT ALL ON demo.* TO 'demo_user'@'localhost';
-- FLUSH PRIVILEGES;

CREATE TABLE user (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  username VARCHAR(60) NOT NULL UNIQUE,
  email VARCHAR(513) NOT NULL,
  pwd VARCHAR(100) NOT NULL,
  pwd_salt VARCHAR(48)  NOT NULL
);

create unique index uc_username_index on user (username);