CREATE EXTENSION IF NOT EXISTS pgcrypto;

ALTER TABLE ntail.n1ne_token
ADD COLUMN n1_token_hash BYTEA;

UPDATE ntail.n1ne_token
SET n1_token_hash = digest(token::text, 'sha256');

ALTER TABLE ntail.n1ne_token
ALTER COLUMN n1_token_hash SET NOT NULL;

ALTER TABLE ntail.n1ne_token
ADD CONSTRAINT unique_n1_token_hash UNIQUE (n1_token_hash);


