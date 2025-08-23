
--liquibase formatted sql

--changeset burgasvv:1
insert into identity(authority, username, password, email, phone, enabled, image_id)
values (
        'ADMIN', 'admin','$2a$10$M8SpDzVU/WO6TZ.mPR2Xpuy.VaOLsyuA8nxY3PVRtPO7zbsz/f/42',
        'admin@gmail.com','+7(999)-123-45-67',true,null
);