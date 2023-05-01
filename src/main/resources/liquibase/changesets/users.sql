--liquibase formatted sql

--changeset natriy : 1
create table users
(
    id         SERIAL PRIMARY KEY,
    username   VARCHAR(255) UNIQUE,
    password   VARCHAR(255),
    first_name VARCHAR(255),
    last_name  VARCHAR(255),
    phone      VARCHAR(255),
    image      BYTEA,
    role       VARCHAR(255),
    enabled    BOOLEAN
);