ALTER TABLE app_user ADD failed_attempt int default 0;
ALTER TABLE app_user ADD lock_time timestamp default null;