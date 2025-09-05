
--liquibase formatted sql

--changeset burgasvv:1
create table if not exists employee (
    id uuid default gen_random_uuid() unique not null ,
    identity_id uuid unique references identity(id) on delete set null on update cascade ,
    first_name varchar not null ,
    last_name varchar not null ,
    patronymic varchar not null ,
    about text unique not null ,
    address_id uuid unique references address(id) on delete set null on update cascade ,
    office_corporation_id uuid not null ,
    office_address_id uuid not null ,
    foreign key (office_corporation_id, office_address_id) references office(corporation_id, address_id) on delete set null on update cascade ,
    position_id uuid references position(id) on delete set null on update cascade
);