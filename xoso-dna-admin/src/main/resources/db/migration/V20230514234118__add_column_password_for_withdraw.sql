create table password_withdraw
(
    id bigint not null generated always as identity,
    user_id bigint not null,
    password character varying(255) not null,
    last_time_password_updated timestamp,
    constraint password_withdraw_pkey primary key (id),
    constraint fk_password_withdraw_user_id foreign key (user_id) references app_user(id)
);
ALTER TABLE client_bank_account ADD deleted boolean default null;
ALTER TABLE master_bank_account ADD deleted boolean default null;

