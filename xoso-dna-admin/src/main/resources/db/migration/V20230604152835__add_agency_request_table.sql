ALTER TABLE wallets ADD total_withdraw decimal(15,2) NOT NULL DEFAULT 0;
ALTER TABLE wallets ADD total_deposit decimal(15,2) NOT NULL DEFAULT 0;
ALTER TABLE wallets ADD total_win decimal(15,2) NOT NULL DEFAULT 0;
ALTER TABLE wallets ADD total_bet decimal(15,2) NOT NULL DEFAULT 0;
