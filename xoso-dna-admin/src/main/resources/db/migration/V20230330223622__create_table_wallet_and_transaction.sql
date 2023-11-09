--alter table app_user add column client_id bigint,
--    add CONSTRAINT fk_client_id FOREIGN KEY (client_id) REFERENCES client(id);
CREATE TABLE wallets
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id bigint,
    balance decimal(15,2) NOT NULL,
    is_master boolean,
    status character varying(255) NOT NULL,
    version INT NOT NULL DEFAULT 0,
    created_date timestamp,
    modified_date timestamp,
    created_by character varying(100),
    modified_by character varying(100),
    CONSTRAINT wallets_pkey PRIMARY KEY (id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES app_user (id)
);


CREATE TABLE bank (
     id bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
     name varchar(255) NOT NULL,
     code varchar(50) NOT NULL,
     description varchar(255),
     enabled bool NULL DEFAULT true,
     created_date timestamp,
     modified_date timestamp,
     created_by varchar(100),
     modified_by varchar(100),
     CONSTRAINT bank_pkey PRIMARY KEY (id)
);

CREATE TABLE configuration
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    code character varying(255) NOT NULL,
    title character varying(255),
    value character varying(255),
    date_value timestamp,
    CONSTRAINT configs_pkey PRIMARY KEY (id)
);

CREATE TABLE master_bank_account (
     id bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
     wallet_id bigint NOT NULL,
     bank_id bigint NOT NULL,
     account_name varchar(255) NOT NULL,
     account_number varchar(150) NOT NULL,
     card_number varchar(150),
     enabled boolean DEFAULT true,
     created_date timestamp,
     modified_date timestamp,
     created_by varchar(100),
     modified_by varchar(100),
     CONSTRAINT master_bank_account_pkey PRIMARY KEY (id),
     CONSTRAINT fk_master_bank_account_wallet_id FOREIGN KEY (wallet_id) REFERENCES wallets(id),
     CONSTRAINT fk_master_bank_account_bank_id FOREIGN KEY (bank_id) REFERENCES bank(id)
);

CREATE TABLE client_bank_account (
     id bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
     user_id bigint NOT NULL,
     bank_id bigint NOT NULL,
     account_name varchar(255) NOT NULL,
     account_number varchar(255) NOT NULL,
     card_number varchar(255),
     enabled boolean DEFAULT true,

     created_date timestamp,
     modified_date timestamp,
     created_by varchar(100),
     modified_by varchar(100),
     CONSTRAINT client_bank_account_pkey PRIMARY KEY (id),
     CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES app_user(id),
     CONSTRAINT fk_bank_id FOREIGN KEY (bank_id) REFERENCES bank(id)
);

CREATE TABLE wallet_transactions
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    transaction_no character varying(100) NOT NULL,
    wallet_master_id bigint NOT NULL,
    wallet_id bigint NOT NULL,
    user_id bigint,
    amount decimal(15,2) NOT NULL,
    fee decimal(15,2),
    status character varying(255) NOT NULL,
    transaction_type character varying(255) NOT NULL,
    content character varying(255) NOT NULL,
    client_bank_account_id bigint,
    master_bank_account_id bigint,
    note character varying(1000),
    version INT NOT NULL DEFAULT 0,

    created_date timestamp,
    modified_date timestamp,
    created_by character varying(100),
    modified_by character varying(100),

    CONSTRAINT wallet_transactions_pkey PRIMARY KEY (id),
    CONSTRAINT fk_wallet_master_id FOREIGN KEY (wallet_master_id) REFERENCES wallets (id),
    CONSTRAINT fk_wallet_id FOREIGN KEY (wallet_id) REFERENCES wallets (id),
    CONSTRAINT fk_client_bank_account_id FOREIGN KEY (client_bank_account_id) REFERENCES client_bank_account (id),
    CONSTRAINT fk_master_bank_account_id FOREIGN KEY (master_bank_account_id) REFERENCES master_bank_account (id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES app_user (id)
);


CREATE INDEX idx_bank_code ON bank (code);