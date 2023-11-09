-- "username": "admin"
-- "password": "dna@qeP8"
INSERT INTO app_user(
    username,
    full_name,
    password,
    password_never_expires,
    is_deleted,
    nonexpired,
    nonlocked,
    nonexpired_credentials,
    enabled
) VALUES ('admin', 'Admin', '$2a$10$m0keQA3ualZ9zYau97GSVuixBAweH2X5fW3M0HuRdejjmr5rPrzQy', true, false, true, true, true, true);
INSERT INTO user_role(user_id, role_id) VALUES(1,1);