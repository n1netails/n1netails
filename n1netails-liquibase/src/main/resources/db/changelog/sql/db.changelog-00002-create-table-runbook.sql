------------------------------
-- Table: ntail.runbook
------------------------------
--DROP TABLE IF EXISTS ntail.runbook;

CREATE TABLE IF NOT EXISTS ntail.runbook
(
    id bigint NOT NULL,
    title character varying(255),
    steps oid,
    CONSTRAINT runbook_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS ntail.runbook
    OWNER to postgres;

------------------------------
-- Table: ntail.tail_runbooks
------------------------------
--DROP TABLE IF EXISTS ntail.tail_runbooks;

CREATE TABLE IF NOT EXISTS ntail.tail_runbooks
(
    related_tails_id bigint NOT NULL,
    runbooks_id bigint NOT NULL,
    CONSTRAINT fk_runbooks_runbook_id FOREIGN KEY (runbooks_id)
        REFERENCES ntail.runbook (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_runbooks_tails_id FOREIGN KEY (related_tails_id)
        REFERENCES ntail.tail (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS ntail.tail_runbooks
    OWNER to postgres;

------------------------------
-- Table: ntail.runbook_related_tail_types
------------------------------
--DROP TABLE IF EXISTS ntail.runbook_related_tail_types;

CREATE TABLE IF NOT EXISTS ntail.runbook_related_tail_types
(
    runbook_id bigint NOT NULL,
    tail_type_id bigint NOT NULL,
    CONSTRAINT fk_runbook_related_tail_types_runbook_id FOREIGN KEY (runbook_id)
        REFERENCES ntail.runbook (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_runbook_related_tail_types_tail_type_id FOREIGN KEY (tail_type_id)
        REFERENCES ntail.tail_type (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS ntail.runbook_related_tail_types
    OWNER to postgres;