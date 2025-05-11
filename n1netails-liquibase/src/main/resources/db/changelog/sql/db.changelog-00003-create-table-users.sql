------------------------------
-- Table: ntail.users
------------------------------
--DROP TABLE IF EXISTS ntail.users;

CREATE TABLE IF NOT EXISTS ntail.users
(
    enabled boolean NOT NULL,
    is_active boolean NOT NULL,
    is_not_locked boolean NOT NULL,
    id bigint NOT NULL,
    join_date timestamp(6) without time zone,
    last_login_date timestamp(6) without time zone,
    last_login_date_display timestamp(6) without time zone,
    email character varying(255) COLLATE pg_catalog."default",
    first_name character varying(255) COLLATE pg_catalog."default",
    last_name character varying(255) COLLATE pg_catalog."default",
    password character varying(255) COLLATE pg_catalog."default",
    profile_image_url character varying(255) COLLATE pg_catalog."default",
    role character varying(255) COLLATE pg_catalog."default",
    user_id character varying(255) COLLATE pg_catalog."default",
    username character varying(255) COLLATE pg_catalog."default",
    authorities character varying(255)[] COLLATE pg_catalog."default",
    CONSTRAINT users_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS ntail.users
    OWNER to postgres;
