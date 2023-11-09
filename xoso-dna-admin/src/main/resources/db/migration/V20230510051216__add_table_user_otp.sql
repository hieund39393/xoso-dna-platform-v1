create table app_user_otp
(
    id bigint not null generated always as identity,
    user_id bigint,
    otp varchar(50),
    sent_date timestamp without time zone,
    expired_date timestamp without time zone,
    constraint app_user_otp_pkey primary key (id),
    constraint fk_app_user_otp_user_id foreign key (user_id) references app_user(id)
)