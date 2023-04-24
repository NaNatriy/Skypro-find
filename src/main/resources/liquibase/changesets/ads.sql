--liquibase formatted sql

--changeset natriy : 2
CREATE TABLE ads
(
    pk          SERIAL PRIMARY KEY,
    author_id   INTEGER REFERENCES users (id),
    title       VARCHAR(255),
    description VARCHAR(255),
    price       INTEGER,
    image       BYTEA
);