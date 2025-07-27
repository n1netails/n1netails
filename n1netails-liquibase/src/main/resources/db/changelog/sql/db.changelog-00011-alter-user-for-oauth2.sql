ALTER TABLE ntail.users
ADD COLUMN provider character varying(50);

ALTER TABLE ntail.users
ADD COLUMN provider_id character varying(255);

ALTER TABLE ntail.users
ADD COLUMN email_verified boolean DEFAULT false;
