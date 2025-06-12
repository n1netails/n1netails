------------------------------
-- Table: ntail.n1ne_token
------------------------------
--DROP TABLE IF EXISTS ntail.n1ne_token;

CREATE TABLE IF NOT EXISTS ntail.n1ne_token
(
    id bigint NOT NULL,
    token UUID NOT NULL UNIQUE,
    user_id bigint NOT NULL,
    organization_id bigint NULL,
    name character varying(255),
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    last_used_at TIMESTAMP,
    CONSTRAINT token_pkey PRIMARY KEY (id),
    CONSTRAINT fk_token_user_id FOREIGN KEY (user_id)
            REFERENCES ntail.users (id) MATCH SIMPLE
            ON UPDATE NO ACTION
            ON DELETE NO ACTION,
    CONSTRAINT fk_token_organization_id FOREIGN KEY (organization_id)
            REFERENCES ntail.organization (id) MATCH SIMPLE
            ON UPDATE NO ACTION
            ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS ntail.n1ne_token
    OWNER to ninetails;

-- Crate Sequence token_seq
CREATE SEQUENCE IF NOT EXISTS token_seq START WITH 1 INCREMENT BY 1;
