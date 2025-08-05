CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

ALTER TABLE ntail.users
ALTER COLUMN user_id TYPE character varying(36);

ALTER TABLE ntail.users
ALTER COLUMN user_id SET DEFAULT uuid_generate_v4()::text;

-- update existing users with new uuid
UPDATE ntail.users
SET user_id = uuid_generate_v4()::text;
