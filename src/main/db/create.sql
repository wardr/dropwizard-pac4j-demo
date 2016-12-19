-- SQL to create tables, etc used by demo app
--
-- To set-up database and user:
--
-- drop database if exists demo;
-- create database demo;
-- CREATE USER 'demo_user'@'localhost' IDENTIFIED BY 'demopassword';
-- GRANT ALL ON demo.* TO 'demo_user'@'localhost';
-- FLUSH PRIVILEGES;

drop table if EXISTS user_role;
drop table if EXISTS role_permission;
drop table if EXISTS permission;
drop table if EXISTS role;
drop table if EXISTS user;

CREATE TABLE user (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  username VARCHAR(60) NOT NULL UNIQUE,
  email VARCHAR(513) NOT NULL,
  pwd VARCHAR(100) NOT NULL,
  pwd_salt VARCHAR(48)  NOT NULL
);

create table role (
  role_id INT PRIMARY KEY,
  name varchar(50) unique not null,
  display_name varchar(50) not null,
  description varchar(255) not null
);

-- permission name should be in the form resource:action, for example 'article:edit'
create table permission (
  permission_id INT PRIMARY KEY,
  name varchar(100) unique not null,
  description varchar(255)
);


create table role_permission (
  role_id int not null references role(role_id) on delete cascade,
  permission_id int not null references permission(permission_id) on delete cascade,
  primary key (role_id, permission_id)
);

create table user_role (
  user_id int not null references user(user_id) on delete cascade,
  role_id int not null references role(role_id) on delete cascade,
  primary key (user_id, role_id)
);

-- fetch all the roles and permissions for each user
CREATE OR REPLACE VIEW user_permission AS
  SELECT
    ur.user_id as user_id,
    r.name AS role_name,
    p2.permission_name
  FROM user_role AS ur
    LEFT JOIN role r ON (r.role_id = ur.role_id)
    LEFT JOIN
    (SELECT
       rp.role_id AS role_id,
       p.name AS permission_name
     FROM role_permission AS rp
       LEFT JOIN permission p ON p.permission_id = rp.permission_id) AS p2
      ON r.role_id = p2.role_id;

-- Create the roles and permissions used by the app
INSERT INTO role (role_id, name, display_name, description)
VALUES (1, 'superuser', 'superuser', 'Administrator');

INSERT INTO permission (permission_id, name, description)
VALUES (1, 'admin:all', 'Full access to admin');

INSERT INTO role_permission (role_id, permission_id) VALUES (1, 1);
