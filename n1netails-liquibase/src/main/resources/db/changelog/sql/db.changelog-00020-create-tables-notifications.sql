CREATE SEQUENCE notification_config_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE notification_config (
    id BIGINT PRIMARY KEY DEFAULT nextval('notification_config_seq'),
    token_id BIGINT NOT NULL,
    platform VARCHAR(255) NOT NULL,
    details TEXT NOT NULL,
    CONSTRAINT fk_notification_config_token
        FOREIGN KEY (token_id) REFERENCES n1ne_token (id)
);

CREATE SEQUENCE user_notification_preference_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE user_notification_preference (
    id BIGINT PRIMARY KEY DEFAULT nextval('user_notification_preference_seq'),
    user_id BIGINT NOT NULL,
    platform VARCHAR(255) NOT NULL,
    CONSTRAINT fk_user_notification_preference_user 
        FOREIGN KEY (user_id) REFERENCES users (id)
);