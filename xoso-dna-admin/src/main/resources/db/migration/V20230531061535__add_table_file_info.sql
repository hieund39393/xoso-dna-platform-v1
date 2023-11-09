create table file_info
(
    id bigint not null generated always as identity,
    file_name varchar(500) NOT NULL,
    url varchar(225),

    created_date timestamp,
    modified_date timestamp,
    created_by varchar(100),
    modified_by varchar(100),
    constraint file_info_pkey primary key (id)
);
CREATE INDEX idx_file_info_file_name ON file_info (file_name);
ALTER TABLE template_content ADD banner TEXT NULL;