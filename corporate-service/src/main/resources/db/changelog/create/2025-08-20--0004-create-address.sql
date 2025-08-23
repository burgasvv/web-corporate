
--liquibase formatted sql

--changeset burgasvv:1
create table if not exists address (
    id uuid default gen_random_uuid() unique not null ,
    country varchar not null ,
    city varchar not null ,
    street varchar not null ,
    house varchar not null ,
    apartment varchar
);