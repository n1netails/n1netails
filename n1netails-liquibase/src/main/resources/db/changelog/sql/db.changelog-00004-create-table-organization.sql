------------------------------
-- Table: ntail.organization
------------------------------
--DROP TABLE IF EXISTS ntail.organization;

CREATE TABLE IF NOT EXISTS ntail.organization
(
    created_at timestamp(6) without time zone,
    id bigint NOT NULL,
    updated_at timestamp(6) without time zone,
    address character varying(255),
    description character varying(255),
    name character varying(255),
    CONSTRAINT organization_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS ntail.organization
    OWNER to n1netails;

------------------------------
-- Table: ntail.user_organizations
------------------------------
--DROP TABLE IF EXISTS ntail.user_organizations;

CREATE TABLE IF NOT EXISTS ntail.user_organizations
(
    organization_id bigint NOT NULL,
    user_id bigint NOT NULL,
    CONSTRAINT user_organizations_pkey PRIMARY KEY (organization_id, user_id),
    CONSTRAINT fk_user_organizations_organization_id FOREIGN KEY (organization_id)
        REFERENCES ntail.organization (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_user_organizations_user_id FOREIGN KEY (user_id)
        REFERENCES ntail.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS ntail.user_organizations
    OWNER to n1netails;