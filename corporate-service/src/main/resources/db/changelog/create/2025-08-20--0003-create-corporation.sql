
--liquibase formatted sql

--changeset burgasvv:1
create table if not exists corporation (
    id uuid default gen_random_uuid() unique not null ,
    name varchar unique not null ,
    description text unique not null ,
    offices_amount bigint not null default 0,
    employees_amount bigint not null default 0 ,
    directors uuid[] not null ,
    image_id uuid unique references media(id) on delete set null on update cascade
);