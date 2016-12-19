-- Create a user with username "admin" and password "adminpassword"
INSERT INTO user (user_id, username, email, pwd, pwd_salt)
VALUES (1, 'admin', 'admin@example.com', 'z0wmz0eoXY+YFZikGv+0NAlhC3RscchDThZJBJv5fzs=', 'Aq5hfr34Y1cFAC6Tmzx6kQ==');

INSERT INTO user_role (user_id, role_id) VALUES (1, 1);