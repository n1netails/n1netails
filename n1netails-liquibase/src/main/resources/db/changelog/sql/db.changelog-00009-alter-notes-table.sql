ALTER TABLE ntail.note DROP COLUMN IF EXISTS organization_id;
ALTER TABLE ntail.note ADD COLUMN organization_id bigint;
ALTER TABLE ntail.note
    ADD CONSTRAINT fk_tail_organization_id FOREIGN KEY (organization_id)
        REFERENCES ntail.organization (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION;

ALTER TABLE ntail.note ADD COLUMN n1 boolean NOT NULL DEFAULT false;
ALTER TABLE ntail.note ADD COLUMN is_human boolean NOT NULL DEFAULT false;
ALTER TABLE ntail.note ADD COLUMN llm_provider character varying(255);
ALTER TABLE ntail.note ADD COLUMN llm_model character varying(255);
