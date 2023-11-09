create table gift_code
(
    id bigint not null generated always as identity,
    user_id bigint,
    code varchar(50),
    number_of_users int default 0,
    created_date timestamp,
    modified_date timestamp,
    created_by varchar(100),
    modified_by varchar(100),

    constraint fk_user_id foreign key (user_id) references app_user(id)
)