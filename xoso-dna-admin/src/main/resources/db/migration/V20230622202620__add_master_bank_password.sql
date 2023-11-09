CREATE INDEX wallet_transactions_content_idx ON wallet_transactions ("content");
ALTER TABLE master_bank_account ADD "password" varchar(150) NULL;