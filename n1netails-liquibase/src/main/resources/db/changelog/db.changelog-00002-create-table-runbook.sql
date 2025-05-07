------------------------------
-- Table: public.runbook
------------------------------
--DROP TABLE IF EXISTS public.runbook;

CREATE TABLE IF NOT EXISTS public.runbook
(
    id bigint NOT NULL,
    title character varying(255) COLLATE pg_catalog."default",
    steps oid,
    CONSTRAINT runbook_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.runbook
    OWNER to postgres;

------------------------------
-- Table: public.tail_runbooks
------------------------------
--DROP TABLE IF EXISTS public.tail_runbooks;

CREATE TABLE IF NOT EXISTS public.tail_runbooks
(
    related_tails_id bigint NOT NULL,
    runbooks_id bigint NOT NULL,
    CONSTRAINT fk5qhab40kcb3dkpjdkhvbdmehr FOREIGN KEY (runbooks_id)
        REFERENCES public.runbook (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkh80uq4erxktgw73di9n6r1yt8 FOREIGN KEY (related_tails_id)
        REFERENCES public.tail (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.tail_runbooks
    OWNER to postgres;

------------------------------
-- Table: public.runbook_related_tail_types
------------------------------
--DROP TABLE IF EXISTS public.runbook_related_tail_types;

CREATE TABLE IF NOT EXISTS public.runbook_related_tail_types
(
    related_tail_types_id bigint NOT NULL,
    runbook_id bigint NOT NULL,
    CONSTRAINT fk_runbook_related_tail_types_runbook_id FOREIGN KEY (runbook_id)
        REFERENCES public.runbook (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_runbook_related_tail_types_tail_types_id FOREIGN KEY (related_tail_types_id)
        REFERENCES public.tail_type (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.runbook_related_tail_types
    OWNER to postgres;