INSERT INTO user_roles(id, user_role, description)
values ('1','ADMIN', 'Admin Permissions'),
       ('2','USER', 'Simple User Permissions');

INSERT INTO users (id, email, first_name, active, last_name, password, username, created_at, last_modified, user_role_id)
VALUES
    ('1', 'admin@admin.com', 'Admin', 1, 'Adminov', '$2a$10$uLYimymH.0qx1cFGMLdqau8FGkAv3zxiRYGp/skhrqHLCPhdgN37G', 'admin',  '2023-08-25T15:14:40.733400800', '2023-08-25T15:14:40.733400800', '1'),
    ('2', 'user@user.com', 'User', 1, 'Userov', '$2a$10$QnuZfkRUcaMzXbw0Rs4cA.nL.7iRS8Mv2fN1Su4g0nqOeg.nVukEW', 'user', '2023-08-25T15:14:40.733400800', '2023-08-25T15:14:40.733400800', '2');


