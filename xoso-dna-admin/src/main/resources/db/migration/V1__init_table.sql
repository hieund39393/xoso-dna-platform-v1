
CREATE TABLE staff
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    is_active boolean NOT NULL,
    display_name character varying(100),
    email_address character varying(50),
    firstname character varying(50),
    joining_date timestamp without time zone,
    lastname character varying(50),
    mobile_no character varying(50),
    national_id character varying(100),
    created_date timestamp without time zone,
    modified_date timestamp without time zone,
    created_by character varying(100),
    modified_by character varying(100),

    CONSTRAINT staff_pkey PRIMARY KEY (id),
    CONSTRAINT uk_email_address UNIQUE (email_address),
    CONSTRAINT uk_mobile_no UNIQUE (mobile_no)
);

-- Table: user
CREATE TABLE app_user
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    username character varying(100) NOT NULL,
    full_name character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    password_never_expires boolean NOT NULL,
    email character varying(100),
    mobile_no character varying(50),
    is_deleted boolean NOT NULL,
    secret_key character varying(255),
    staff_id bigint,
    client_id bigint,
    nonexpired boolean NOT NULL,
    nonlocked boolean NOT NULL,
    nonexpired_credentials boolean NOT NULL,
    enabled boolean NOT NULL,
    last_time_password_updated timestamp without time zone,
    deleted_date timestamp without time zone,
    created_date timestamp without time zone,
    modified_date timestamp without time zone,
    created_by character varying(100),
    modified_by character varying(100),
    CONSTRAINT app_user_pkey PRIMARY KEY (id),
    CONSTRAINT username_org UNIQUE (username),
    CONSTRAINT fk_user_staff FOREIGN KEY (staff_id) REFERENCES staff (id) MATCH SIMPLE
);

CREATE TABLE role
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    description character varying(500) NOT NULL,
    is_disabled boolean NOT NULL,
    name character varying(100) NOT NULL,
    created_date timestamp without time zone,
    modified_date timestamp without time zone,
    created_by character varying(100),
    modified_by character varying(100),
    CONSTRAINT role_pkey PRIMARY KEY (id),
    CONSTRAINT uk_name UNIQUE (name)
);

CREATE TABLE permission
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    action_name character varying(100),
    code character varying(100) NOT NULL,
    entity_name character varying(100),
    grouping character varying(45) NOT NULL,
    created_date timestamp without time zone,
    modified_date timestamp without time zone,
    created_by character varying(100),
    modified_by character varying(100),
    CONSTRAINT permission_pkey PRIMARY KEY (id)
);

CREATE TABLE role_permission
(
    role_id bigint NOT NULL,
    permission_id bigint NOT NULL,
    CONSTRAINT role_permission_pkey PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_id FOREIGN KEY (role_id)
        REFERENCES role (id),
    CONSTRAINT fk_permission_id FOREIGN KEY (permission_id)
        REFERENCES permission (id)
);

CREATE TABLE user_role
(
    user_id bigint NOT NULL,
    role_id bigint NOT NULL,
    CONSTRAINT user_role_pkey PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_role_id FOREIGN KEY (role_id)
        REFERENCES role (id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id)
        REFERENCES app_user (id)
);

CREATE TABLE client
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    account_no character varying(20) NOT NULL,
    status character varying(50) NOT NULL,
    firstname character varying(50),
    lastname character varying(50),
    full_name character varying(100),
    mobile_no character varying(50) NOT NULL,
    national_id character varying(100),
    email_address character varying(50),
    is_staff boolean NOT NULL,
    date_of_birth timestamp without time zone,
    gender character varying(50),
    activation_date timestamp without time zone,
    joining_date timestamp without time zone,
    created_date timestamp without time zone,
    modified_date timestamp without time zone,
    created_by character varying(100),
    modified_by character varying(100),

    CONSTRAINT client_pkey PRIMARY KEY (id),
    CONSTRAINT client_uk_email_address UNIQUE (email_address),
    CONSTRAINT client_uk_national_id UNIQUE (national_id),
    CONSTRAINT client_uk_mobile_no UNIQUE (mobile_no)
);

CREATE TABLE address
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    latitude decimal,
    longitude decimal,
    country_iso_code character varying(50),
    province_iso_code character varying(50),
    district_iso_code character varying(50),
    ward_iso_code character varying(50),

    country character varying(50),
    province character varying(50),
    district character varying(50),
    ward character varying(50),

    address_line_1 character varying(500),
    address_line_2 character varying(500),
    address_line_3 character varying(500),

    created_date timestamp without time zone,
    modified_date timestamp without time zone,
    created_by character varying(100),
    modified_by character varying(100),

    CONSTRAINT address_pkey PRIMARY KEY (id)

);

CREATE TABLE client_address
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    client_id bigint NOT NULL,
    address_id bigint NOT NULL,
    addressType character varying(50),
    is_active boolean NOT NULL,

    created_date timestamp without time zone,
    modified_date timestamp without time zone,
    created_by character varying(100),
    modified_by character varying(100),

    CONSTRAINT client_address_pkey PRIMARY KEY (id)
);

CREATE INDEX idx_staff_created_by ON staff (created_by);
CREATE INDEX idx_staff_modified_by ON staff (modified_by);

CREATE INDEX idx_app_user_created_by ON app_user (created_by);
CREATE INDEX idx_app_user_modified_by ON app_user (modified_by);

CREATE INDEX idx_role_created_by ON role (created_by);
CREATE INDEX idx_role_modified_by ON role (modified_by);

CREATE INDEX idx_permission_created_by ON permission (created_by);
CREATE INDEX idx_permission_modified_by ON permission (modified_by);
