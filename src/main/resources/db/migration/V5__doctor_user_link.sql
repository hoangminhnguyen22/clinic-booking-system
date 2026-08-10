ALTER TABLE doctors
    ADD COLUMN user_id BIGINT;

ALTER TABLE doctors
    ADD CONSTRAINT uk_doctors_user_id UNIQUE (user_id);

ALTER TABLE doctors
    ADD CONSTRAINT fk_doctors_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE SET NULL;