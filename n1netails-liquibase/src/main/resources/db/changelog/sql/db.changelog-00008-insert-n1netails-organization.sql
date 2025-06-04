INSERT INTO ntail.organization (id, name, description, address, created_at, updated_at)
VALUES (nextval('ntail.organization_seq'), 'n1netails', 'Default n1netails organization', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;
-- Assuming 'name' should be unique and using 'organization_seq' for the ID.
-- Added ON CONFLICT to prevent errors if run multiple times, though Liquibase should handle this.
