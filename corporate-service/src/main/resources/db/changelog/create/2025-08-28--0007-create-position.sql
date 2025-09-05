
--liquibase formatted sql

--changeset burgasvv:1
create table if not exists position (
    id uuid default gen_random_uuid() unique not null ,
    name varchar not null ,
    description text not null ,
    department_id uuid references department(id) on delete set null on update cascade
);