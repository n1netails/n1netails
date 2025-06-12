DROP TABLE IF EXISTS ntail.user_organizations CASCADE;
DROP TABLE IF EXISTS ntail.organization CASCADE;
DROP TABLE IF EXISTS ntail.users CASCADE;
DROP TABLE IF EXISTS ntail.runbook_related_tail_types CASCADE;
DROP TABLE IF EXISTS ntail.tail_runbooks CASCADE;
DROP TABLE IF EXISTS ntail.runbook CASCADE;
DROP TABLE IF EXISTS ntail.note CASCADE;
DROP TABLE IF EXISTS ntail.tail_variable CASCADE;
DROP TABLE IF EXISTS ntail.tail CASCADE;
DROP TABLE IF EXISTS ntail.tail_level CASCADE;
DROP TABLE IF EXISTS ntail.tail_type CASCADE;
DROP TABLE IF EXISTS ntail.tail_status CASCADE;
DROP TABLE IF EXISTS ntail.n1ne_token CASCADE;

--DROP TABLE IF EXISTS ntail.databasechangelog CASCADE;
--DROP TABLE IF EXISTS ntail.databasechangeloglock CASCADE;

CREATE SCHEMA IF NOT EXISTS ntail
    AUTHORIZATION ninetails;

-- Set UTC time
ALTER DATABASE n1netails SET timezone TO 'Etc/UTC';
