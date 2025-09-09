------------------------------
-- Table: ntail.github_installation
------------------------------
--DROP TABLE IF EXISTS ntail.github_installation;

CREATE TABLE IF NOT EXISTS ntail.github_installation
(
    id bigint NOT NULL,
    organization_id bigint NOT NULL,
    installation_id character varying(255) NOT NULL,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    CONSTRAINT github_installation_pkey PRIMARY KEY (id),
    CONSTRAINT fk_github_installation_organization_id FOREIGN KEY (organization_id)
        REFERENCES ntail.organization (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT uk_github_installation_organization_id UNIQUE (organization_id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS ntail.github_installation
    OWNER to n1netails;
