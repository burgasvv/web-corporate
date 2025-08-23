
--liquibase formatted sql

--changeset burgasvv:1
create table if not exists office (
    corporation_id uuid references corporation(id) on delete cascade on update cascade ,
    address_id uuid unique references address(id) on delete set null on update cascade ,
    employees_amount bigint not null default 0 ,
    primary key (corporation_id, address_id)
);