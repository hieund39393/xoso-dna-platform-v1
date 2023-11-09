create table request_command_source
(
    id bigint not null generated always as identity,
    entity_name varchar(50) NOT NULL,
    action_name varchar(50) NOT NULL,
    command_as_json TEXT NOT NULL,
    checker boolean default false,
    checker_on_date timestamp,
    resource_id bigint,
    created_date timestamp,
    modified_date timestamp,
    created_by varchar(100),
    modified_by varchar(100),
    constraint request_command_source_pkey primary key (id)
)