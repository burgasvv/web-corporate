
--liquibase formatted sql

--changeset burgasvv:1
create table if not exists identity (
    id uuid default gen_random_uuid() unique not null ,
    authority varchar not null ,
    username varchar unique not null ,
    password varchar not null ,
    email varchar unique not null ,
    phone varchar unique not null ,
    enabled boolean not null ,
    image_id uuid unique references media(id) on delete set null on update cascade
);

--changeset burgasvv:2
alter table identity
    add unique (image_id);
