-- Create table for user authenticators
CREATE TABLE ntail.user_authenticators (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id bigint NOT NULL,
    name VARCHAR(255) NOT NULL,
    rp_id VARCHAR(255) NOT NULL, -- Relying Party ID
    credential_id BYTEA NOT NULL UNIQUE,
    public_key BYTEA NOT NULL,
    attestation_type VARCHAR(255),
    aaguid UUID,
    sign_count BIGINT NOT NULL,
    transports VARCHAR(255), -- Comma-separated list of transports (e.g., "usb,nfc,ble")
    backup_eligible BOOLEAN DEFAULT FALSE,
    backup_state BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_used_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_user_authenticators_user_id FOREIGN KEY (user_id) REFERENCES ntail.users(id) ON DELETE CASCADE
);

COMMENT ON TABLE ntail.user_authenticators IS 'Stores information about WebAuthn authenticators registered by users.';
COMMENT ON COLUMN ntail.user_authenticators.rp_id IS 'Relying Party ID for which the authenticator is registered.';
COMMENT ON COLUMN ntail.user_authenticators.credential_id IS 'The globally unique ID for this credential.';
COMMENT ON COLUMN ntail.user_authenticators.public_key IS 'The public key of the credential.';
COMMENT ON COLUMN ntail.user_authenticators.attestation_type IS 'The type of attestation used during registration (e.g., "none", "packed").';
COMMENT ON COLUMN ntail.user_authenticators.aaguid IS 'Authenticator Attestation Globally Unique Identifier.';
COMMENT ON COLUMN ntail.user_authenticators.sign_count IS 'The signature counter of the authenticator.';
COMMENT ON COLUMN ntail.user_authenticators.transports IS 'Comma-separated list of transport methods supported by the authenticator.';
COMMENT ON COLUMN ntail.user_authenticators.backup_eligible IS 'Indicates if the authenticator is eligible for backup.';
COMMENT ON COLUMN ntail.user_authenticators.backup_state IS 'Indicates if the authenticator is currently backed up.';

-- Create table for user credentials (deprecated by some WebAuthn libraries, but useful for Yubico's)
-- This table structure is based on Yubico's example, it might store redundant info with user_authenticators
-- but Yubico's library might rely on a specific structure.
-- For this iteration, we will primarily use user_authenticators and keep this simpler.
-- If specific fields from Yubico's CredentialRepository example are needed, they can be added here.
CREATE TABLE ntail.user_credentials (
    credential_id BYTEA NOT NULL,
    user_handle BYTEA NOT NULL,
    public_key_cose BYTEA NOT NULL,
    signature_count BIGINT NOT NULL,
    user_id UUID NOT NULL, -- Added to link back to the user
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, credential_id), -- Composite key
    CONSTRAINT fk_user_credentials_user_id FOREIGN KEY (user_id) REFERENCES ntail.users(id) ON DELETE CASCADE
);

COMMENT ON TABLE ntail.user_credentials IS 'Stores WebAuthn credential information. Note: Yubico library might expect specific fields/tables.';
COMMENT ON COLUMN ntail.user_credentials.credential_id IS 'The credential ID (same as in user_authenticators).';
COMMENT ON COLUMN ntail.user_credentials.user_handle IS 'The user handle associated with the credential.';
COMMENT ON COLUMN ntail.user_credentials.public_key_cose IS 'The public key in COSE format.';
COMMENT ON COLUMN ntail.user_credentials.signature_count IS 'The signature counter for the credential.';

-- Indexes for performance
CREATE INDEX idx_user_authenticators_user_id ON ntail.user_authenticators(user_id);
CREATE INDEX idx_user_credentials_user_id_credential_id ON ntail.user_credentials(user_id, credential_id);

-- Sequence for passkey identifiers if needed (though UUIDs are used for IDs)
-- CREATE SEQUENCE ntail.passkey_sequence START 1 INCREMENT 1;

-- Grant permissions
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE ntail.user_authenticators TO GROUP ntail_group_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE ntail.user_credentials TO GROUP ntail_group_app;
-- GRANT USAGE, SELECT ON SEQUENCE ntail.passkey_sequence TO GROUP ntail_group_app; -- If sequence is used
ALTER TABLE ntail.user_authenticators OWNER TO ntail_owner;
ALTER TABLE ntail.user_credentials OWNER TO ntail_owner;
-- ALTER SEQUENCE ntail.passkey_sequence OWNER TO ntail_owner; -- If sequence is used
