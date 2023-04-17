--liquibase formatted sql

--changeset Izentall:create-user-table
CREATE TABLE "user"
(
    id serial PRIMARY KEY,
    username varchar(100) NOT NULL,
    chat_id bigint NOT NULL
);

--changeset Izentall:create-settings-table
CREATE TABLE "settings"
(
    id serial PRIMARY KEY,
    user_id integer NOT NULL,
    user_login varchar(100) NOT NULL,
    host varchar(100) NOT NULL,
    port integer NOT NULL,
    "password" varchar(100)
);
