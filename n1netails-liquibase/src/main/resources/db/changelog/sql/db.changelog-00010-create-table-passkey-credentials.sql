CREATE SEQUENCE ntail.passkey_credentials_seq
    INCREMENT BY 1
    START WITH 1
    NO CYCLE;

CREATE TABLE ntail.passkey_credentials (
    id BIGINT NOT NULL DEFAULT nextval('ntail.passkey_credentials_seq'),
    user_id BIGINT NOT NULL,
    credential_id BYTEA NOT NULL UNIQUE,
    public_key_cose BYTEA NOT NULL,
    signature_count BIGINT NOT NULL,
    user_handle BYTEA NOT NULL,
    attestation_type VARCHAR(255),
    registered_at TIMESTAMP WITHOUT TIME ZONE,
    last_used_at TIMESTAMP WITHOUT TIME ZONE,
    uv_initialized BOOLEAN,
    backup_eligible BOOLEAN,
    backup_state BOOLEAN,
    device_name VARCHAR(255),
    aaguid UUID,
    attestation_object VARCHAR(2048),
    CONSTRAINT pk_passkey_credentials PRIMARY KEY (id),
    CONSTRAINT uq_passkey_credential_id UNIQUE (credential_id)
);

ALTER TABLE ntail.passkey_credentials
    ADD CONSTRAINT fk_passkey_credentials_user
    FOREIGN KEY (user_id)
    REFERENCES ntail.users (id);

CREATE TABLE ntail.passkey_credential_transports (
    passkey_id BIGINT NOT NULL,
    transport VARCHAR(255) NOT NULL
);

ALTER TABLE ntail.passkey_credential_transports
    ADD CONSTRAINT fk_passkey_transports_credential
    FOREIGN KEY (passkey_id)
    REFERENCES ntail.passkey_credentials (id)
    ON DELETE CASCADE;

ALTER TABLE ntail.passkey_credential_transports
    ADD CONSTRAINT pk_passkey_credential_transports
    PRIMARY KEY (passkey_id, transport);
