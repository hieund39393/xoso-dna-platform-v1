create table agency_request
(
    id bigint not null generated always as identity,

    user_id bigint not null,
    full_name varchar(100) not null,
    email varchar(100) not null,
    bank_id bigint not null,
    bank_account_name varchar(255) not null,
    bank_account_number varchar(255) not null,
    note varchar(500),
    status int not null,

    created_date timestamp,
    modified_date timestamp,
    created_by varchar(100),
    modified_by varchar(100),
    constraint agency_request_pkey primary key (id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES app_user(id),
    CONSTRAINT fk_bank_id FOREIGN KEY (bank_id) REFERENCES bank(id)
);

ALTER TABLE app_user ADD agency_id bigint NULL DEFAULT 0;
ALTER TABLE app_user ADD agency_level int  NULL DEFAULT 0;
ALTER TABLE app_user ADD agency_status int NULL DEFAULT 0;
