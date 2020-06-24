DELETE
FROM meals;
DELETE
FROM user_roles;
DELETE
FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles (role, user_id)
VALUES ('ROLE_USER', 100000),
       ('ROLE_ADMIN', 100001);

INSERT INTO meals (description, calories, date_time, user_id)
VALUES ('User breakfast', 500, '2020-01-30 10:00:00-00', 100000),
       ('User lunch', 1000, '2020-01-30 13:00:00-00', 100000),
       ('User dinner', 500, '2020-01-30 20:00:00-00', 100000),
       ('Red lobster', 500, '2020-01-29 10:00:00-00', 100000),
       ('Admin breakfast', 500, '2020-01-31 10:00:00-00', 100001),
       ('Admin lunch', 410, '2020-01-31 13:00:00-00', 100001),
       ('Admin dinner', 410, '2020-01-31 20:00:00-00', 100001),
       ('Special meal', 410, '2020-02-01 23:00:00-00', 100001);


