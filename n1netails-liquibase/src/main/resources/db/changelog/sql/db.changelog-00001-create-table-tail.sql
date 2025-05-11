------------------------------
-- Table: ntail.tail_status
------------------------------
--DROP TABLE IF EXISTS ntail.tail_status;

CREATE TABLE IF NOT EXISTS ntail.tail_status
(
    id bigint NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT tail_status_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS ntail.tail_status
    OWNER to postgres;

------------------------------
-- Table: ntail.tail_type
------------------------------
--DROP TABLE IF EXISTS ntail.tail_type;

CREATE TABLE IF NOT EXISTS ntail.tail_type
(
    id bigint NOT NULL,
    description character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT tail_type_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS ntail.tail_type
    OWNER to postgres;

------------------------------
-- Table: ntail.tail_level
------------------------------
--DROP TABLE IF EXISTS ntail.tail_level;

CREATE TABLE IF NOT EXISTS ntail.tail_level
(
    id bigint NOT NULL,
    description character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT tail_level_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS ntail.tail_level
    OWNER to postgres;

------------------------------
-- Table: ntail.tail
------------------------------
--DROP TABLE IF EXISTS ntail.tail;

CREATE TABLE IF NOT EXISTS ntail.tail
(
    id bigint NOT NULL,
    level_id bigint,
    resolved_timestamp timestamp(6) with time zone,
    status_id bigint,
    "timestamp" timestamp(6) with time zone,
    type_id bigint,
    assigned_user_id character varying(255) COLLATE pg_catalog."default",
    description character varying(255) COLLATE pg_catalog."default",
    title character varying(255) COLLATE pg_catalog."default",
    details text COLLATE pg_catalog."default",
    CONSTRAINT tail_pkey PRIMARY KEY (id),
    CONSTRAINT fk_tail_status_id FOREIGN KEY (status_id)
        REFERENCES ntail.tail_status (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_tail_type_id FOREIGN KEY (type_id)
        REFERENCES ntail.tail_type (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_tail_level_id FOREIGN KEY (level_id)
        REFERENCES ntail.tail_level (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS ntail.tail
    OWNER to postgres;

------------------------------
-- Table: ntail.tail_variable
------------------------------
--DROP TABLE IF EXISTS ntail.tail_variable;

CREATE TABLE IF NOT EXISTS ntail.tail_variable
(
    id bigint NOT NULL,
    tail_id bigint,
    key character varying(255) COLLATE pg_catalog."default",
    value character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT tail_variable_pkey PRIMARY KEY (id),
    CONSTRAINT fk_tail_variable_tail_id FOREIGN KEY (tail_id)
        REFERENCES ntail.tail (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS ntail.tail_variable
    OWNER to postgres;

------------------------------
-- Table: ntail.note
------------------------------
--DROP TABLE IF EXISTS ntail.note;

CREATE TABLE IF NOT EXISTS ntail.note
(
    created_at timestamp(6) with time zone,
    id bigint NOT NULL,
    tail_id bigint,
    content oid,
    CONSTRAINT note_pkey PRIMARY KEY (id),
    CONSTRAINT fk_note_tail_id FOREIGN KEY (tail_id)
        REFERENCES ntail.tail (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS ntail.note
    OWNER to postgres;