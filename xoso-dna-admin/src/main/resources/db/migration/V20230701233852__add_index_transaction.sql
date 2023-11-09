CREATE INDEX idx_wallet_transactions_content ON wallet_transactions (content);
CREATE INDEX idx_wallet_transactions_status ON wallet_transactions (status);

CREATE INDEX idx_app_user_username ON app_user (username);
CREATE INDEX idx_app_user_mobile_no ON app_user (mobile_no);
CREATE INDEX idx_app_user_full_name ON app_user (full_name);