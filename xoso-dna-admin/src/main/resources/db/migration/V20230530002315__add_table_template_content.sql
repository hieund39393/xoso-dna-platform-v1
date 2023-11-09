create table template_content
(
    id bigint not null generated always as identity,
    code varchar(50) NOT NULL,
    name varchar(50) NOT NULL,
    html TEXT NOT NULL,
    active boolean default false,
    category varchar(225),
    language varchar(50),
    start_date timestamp,
    end_date timestamp,

    created_date timestamp,
    modified_date timestamp,
    created_by varchar(100),
    modified_by varchar(100),
    constraint template_content_pkey primary key (id)
);

CREATE INDEX idx_template_content_code ON template_content (code);
CREATE INDEX idx_template_content_language ON template_content (language);
CREATE INDEX idx_template_content_active ON template_content (active);
CREATE INDEX idx_template_content_start_date ON template_content (start_date);
CREATE INDEX idx_template_content_end_date ON template_content (end_date);

CREATE INDEX idx_request_command_source_entity_name ON request_command_source (entity_name);