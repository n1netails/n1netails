-- liquibase formatted sql

-- changeset jules:00010-1
CREATE SEQUENCE ntail.passkey_credentials_seq
    INCREMENT BY 1
    START WITH 1
    NO CYCLE;

-- changeset jules:00010-2
CREATE TABLE ntail.passkey_credentials (
    id BIGINT NOT NULL DEFAULT nextval('ntail.passkey_credentials_seq'),
    user_id BIGINT NOT NULL,
    credential_id VARCHAR(255) NOT NULL,
    public_key_cose BYTEA NOT NULL,
    signature_count BIGINT NOT NULL,
    user_handle VARCHAR(255),
    attestation_type VARCHAR(255),
    registered_at TIMESTAMP WITHOUT TIME ZONE,
    last_used_at TIMESTAMP WITHOUT TIME ZONE,
    uv_initialized BOOLEAN,
    backup_eligible BOOLEAN,
    backup_state BOOLEAN,
    device_name VARCHAR(255),
    aaguid VARCHAR(255),
    attestation_object VARCHAR(2048),
    CONSTRAINT pk_passkey_credentials PRIMARY KEY (id),
    CONSTRAINT uq_passkey_credential_id UNIQUE (credential_id)
);

-- changeset jules:00010-3
ALTER TABLE ntail.passkey_credentials
    ADD CONSTRAINT fk_passkey_credentials_user
    FOREIGN KEY (user_id)
    REFERENCES ntail.users (id);

-- changeset jules:00010-4
CREATE TABLE ntail.passkey_credential_transports (
    credential_id BIGINT NOT NULL, -- Corresponds to PasskeyCredentialEntity id
    transport VARCHAR(255) NOT NULL
);

-- changeset jules:00010-5
ALTER TABLE ntail.passkey_credential_transports
    ADD CONSTRAINT fk_passkey_transports_credential
    FOREIGN KEY (credential_id)
    REFERENCES ntail.passkey_credentials (id)
    ON DELETE CASCADE;

-- changeset jules:00010-6
ALTER TABLE ntail.passkey_credential_transports
    ADD CONSTRAINT pk_passkey_credential_transports
    PRIMARY KEY (credential_id, transport);
