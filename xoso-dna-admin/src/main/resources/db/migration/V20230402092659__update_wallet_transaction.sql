alter table wallet_transactions add column submitted_date timestamp;
alter table wallet_transactions add column approved_date timestamp;
alter table wallet_transactions add column rejected_date timestamp;
alter table wallet_transactions add column approved_by character varying(255);
alter table wallet_transactions add column rejected_by character varying(255);

CREATE INDEX idx_submitted_date ON wallet_transactions (submitted_date);
CREATE INDEX idx_approved_date ON wallet_transactions (approved_date);
CREATE INDEX idx_rejected_date ON wallet_transactions (rejected_date);
CREATE INDEX idx_approved_by ON wallet_transactions (approved_by);
CREATE INDEX idx_rejected_by ON wallet_transactions (rejected_by);
CREATE INDEX idx_account_number ON client_bank_account (account_number);

