------------------------------
-- Table: public.organization
------------------------------
--DROP TABLE IF EXISTS public.organization;

CREATE TABLE IF NOT EXISTS public.organization
(
    created_at timestamp(6) without time zone,
    id bigint NOT NULL,
    updated_at timestamp(6) without time zone,
    address character varying(255) COLLATE pg_catalog."default",
    description character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT organization_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.organization
    OWNER to postgres;

------------------------------
-- Table: public.user_organizations
------------------------------
--DROP TABLE IF EXISTS public.user_organizations;

CREATE TABLE IF NOT EXISTS public.user_organizations
(
    organization_id bigint NOT NULL,
    user_id bigint NOT NULL,
    CONSTRAINT user_organizations_pkey PRIMARY KEY (organization_id, user_id),
    CONSTRAINT fk_user_organizations_organization_id FOREIGN KEY (organization_id)
        REFERENCES public.organization (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_user_organizations_user_id FOREIGN KEY (user_id)
        REFERENCES public.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.user_organizations
    OWNER to postgres;