INSERT INTO ntail.organization (id, name, description, address, created_at, updated_at)
VALUES (nextval('ntail.organization_seq'), 'n1netails', 'Default n1netails organization', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

ALTER TABLE ntail.tail ADD COLUMN organization_id bigint;
ALTER TABLE ntail.tail
    ADD CONSTRAINT fk_tail_organization_id FOREIGN KEY (organization_id)
        REFERENCES ntail.organization (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION;
