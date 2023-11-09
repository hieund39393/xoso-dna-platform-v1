ALTER TABLE lottery_category ADD enable_campaign boolean DEFAULT false;
ALTER TABLE lottery_category ADD total_master_win decimal(15,2) NOT NULL DEFAULT 0;
ALTER TABLE lottery ADD total_master_win decimal(15,2) NOT NULL DEFAULT 0;
