DROP TABLE IF EXISTS ntail.user_organizations;
DROP TABLE IF EXISTS ntail.organization;
DROP TABLE IF EXISTS ntail.users;
DROP TABLE IF EXISTS ntail.runbook_related_tail_types;
DROP TABLE IF EXISTS ntail.tail_runbooks;
DROP TABLE IF EXISTS ntail.runbook;
DROP TABLE IF EXISTS ntail.note;
DROP TABLE IF EXISTS ntail.tail_variable;
DROP TABLE IF EXISTS ntail.tail;
DROP TABLE IF EXISTS ntail.tail_level;
DROP TABLE IF EXISTS ntail.tail_type;
DROP TABLE IF EXISTS ntail.tail_status;

--DROP TABLE IF EXISTS ntail.databasechangelog;
--DROP TABLE IF EXISTS ntail.databasechangeloglock;

CREATE SCHEMA IF NOT EXISTS ntail
    AUTHORIZATION postgres;
