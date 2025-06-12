-- Create the user
CREATE USER ninetails WITH PASSWORD 'ninetails';

-- Grant DB-level access
GRANT CONNECT ON DATABASE n1netails TO ninetails;

-- Create ntail schema and give ninetails user authorization
CREATE SCHEMA IF NOT EXISTS ntail AUTHORIZATION ninetails;
