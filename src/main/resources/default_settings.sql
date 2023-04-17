--liquibase formatted sql

--changeset Izentall:create-default-settings
insert into "settings" (user_id, host, port, user_login, "password")
    values (0, '173.18.0.4', '22', 'root', null);
