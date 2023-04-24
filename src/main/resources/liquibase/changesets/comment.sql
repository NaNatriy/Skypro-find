--liquibase formatted sql

--changeset natriy : 2
create table comment
(
    pk         SERIAL PRIMARY KEY ,
    text       VARCHAR(255),
    created_at TIMESTAMP,
    ads_pk     INTEGER REFERENCES ads (pk),
    author_id  INTEGER REFERENCES users (id)
);