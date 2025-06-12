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
    email character varying(255),
    first_name character varying(255),
    last_name character varying(255),
    password character varying(255),
    profile_image_url character varying(255),
    role character varying(255),
    user_id character varying(255),
    username character varying(255),
    authorities character varying(255)[],
    CONSTRAINT users_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS ntail.users
    OWNER to n1netails;

------------------------------
-- Table: ntail.note
------------------------------
--DROP TABLE IF EXISTS ntail.note;

CREATE TABLE IF NOT EXISTS ntail.note
(
    created_at timestamp(6) with time zone,
    id bigint NOT NULL,
    tail_id bigint,
    user_id bigint NOT NULL,
    content oid,
    CONSTRAINT note_pkey PRIMARY KEY (id),
    CONSTRAINT fk_note_tail_id FOREIGN KEY (tail_id)
        REFERENCES ntail.tail (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_note_user_id FOREIGN KEY (user_id)
        REFERENCES ntail.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS ntail.note
    OWNER to n1netails;
