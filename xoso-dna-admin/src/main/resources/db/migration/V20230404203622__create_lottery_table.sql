-- tao table lien quan den nghiepj vu so xo

-- table luu tru ngon ngu
CREATE TABLE translated_string
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    code VARCHAR(50) NOT NULL,
    lao VARCHAR(500),
    viet VARCHAR(500),
    thai VARCHAR(500),
    cam VARCHAR(500),
    CONSTRAINT translated_string_pkey PRIMARY KEY (id)
);

-------table lottery---------
CREATE TABLE lottery_category
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    name varchar(50) NOT NULL,
    code varchar (225),
    active boolean default true,
    created_date timestamp,
    modified_date timestamp,
    created_by varchar(100),
    modified_by varchar(100),

    CONSTRAINT lottery_category_pkey PRIMARY KEY (id)
);

CREATE TABLE lottery
(
    id bigint not null generated always as identity,
    category_id bigint not null,
    type varchar(50) not null ,
    modes varchar(200),
    name varchar(50) NOT NULL,
    code varchar (225),
    hour int,
    min int,
    sec int,
    active boolean default true,
    created_date timestamp,
    modified_date timestamp,
    created_by varchar(100),
    modified_by varchar(100),

    CONSTRAINT lottery_pkey PRIMARY KEY (id),
    CONSTRAINT fk_category_id FOREIGN KEY (category_id) REFERENCES lottery_category(id)
);

create table lottery_mode
(
    id bigint not null generated always as identity,
    code varchar(50) not null,
    name varchar(50) NOT NULL,
    price bigint not null default 0,
    prize_money bigint not null default 0,
    created_date timestamp,
    modified_date timestamp,
    created_by varchar(100),
    modified_by varchar(100),

    CONSTRAINT lottery_type_pkey PRIMARY KEY (id)
);

create table lottery_session
(
    id bigint not null generated always as identity,
    lottery_id bigint not null,
    status varchar(50),
    result_0 varchar(200),
    result_1 varchar(200),
    result_2 varchar(200),
    result_3 varchar(200),
    result_4 varchar(200),
    result_5 varchar(200),
    result_6 varchar(200),
    result_7 varchar(200),
    start_time bigint,

    constraint lottery_session_pkey primary key (id),
    CONSTRAINT fk_lottery_id FOREIGN KEY (lottery_id) REFERENCES lottery(id)
);

create table lottery_ticket
(
    id bigint not null generated always as identity,
    user_id bigint,
    session_id bigint,
    code varchar(50),
    numbers varchar(1000),
    quantity int default 0,
    price bigint not null default 0,
    prize_money bigint not null default 0,
    status int default 0,
    win boolean default false,

    constraint lottery_ticket_pkey primary key (id),
    constraint fk_user_id foreign key (user_id) references app_user(id),
    constraint fk_session_id foreign key (session_id) references lottery_session(id)
)