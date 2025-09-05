
--liquibase formatted sql

--changeset burgasvv:1
create table if not exists department (
    id uuid default gen_random_uuid() unique not null ,
    name varchar not null ,
    description text not null ,
    corporation_id uuid references corporation(id) on delete cascade on update cascade
);

--changeset burgasvv:2
create table if not exists office_department (
    office_corporation_id uuid not null ,
    office_address_id uuid not null ,
    foreign key (office_corporation_id, office_address_id) references office(corporation_id, address_id) on delete cascade on update cascade ,
    department_id uuid references department(id) on delete cascade on update cascade
);