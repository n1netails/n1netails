# N1netails Liquibase

## Requirements
- Java 17
- PostgreSQL

Set up postgres database create database `n1netails`
Postgres local user

username: n1netails \
password: n1netails

## Create
### Create User
Create the `n1netails` user in postgres pgAdmin 4 if none exists or use the following command to create the user:
```sql
CREATE USER n1netails WITH PASSWORD 'n1netails';
```

### Create Database
Create Database `n1netails` in postgres pgAdmin 4 if none exists or use the following command to create database:
```sql
CREATE DATABASE "n1netails"
    WITH
    OWNER = n1netails
    ENCODING = 'UTF8'
    LOCALE_PROVIDER = 'libc'
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;
```

### Create Schema
Create Schema `ntail` inside of the postgres `n1netails` Database with pgAdmin 4 if none exists or use the following command to create schema:
```sql
CREATE SCHEMA IF NOT EXISTS ntail
    AUTHORIZATION n1netails;
```

### Set UTC time
Set the n1netails database to timezone UTC
```sql
ALTER DATABASE n1netails SET timezone TO 'Etc/UTC';
```

## Build
Build the project using the following command
```bash
mvn clean install
```

## Run
Run the project on your local computer using the following command
```bash
mvn spring-boot:run
```

Run the project with environment variables.
- POSTGRES_URL (postgres database url)
- POSTGRES_USERNAME (n1netails user)
- POSTGRES_PASSWORD (n1netails user password)

Example:
```bash
mvn spring-boot:run -DSPRING_PROFILE_ACTIVE=local -DPOSTGRES_URL=jdbc:postgresql://localhost/n1netails -DPOSTGRES_USERNAME=n1netails -DPOSTGRES_PASSWORD=n1netails
```

## Liquibase Maven Commands

Rollback (example of rolling back 2 times)
```bash
mvn liquibase:rollback -Dliquibase.rollbackCount=2
```

Clear Check Sums
```bash
mvn liquibase:clearCheckSums
```

Update
```bash
mvn liquibase:update
```

## Drop all tables and sequencies
```sql
-- n1netails tables
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

-- liquibase tables
DROP TABLE IF EXISTS ntail.databasechangelog CASCADE;
DROP TABLE IF EXISTS ntail.databasechangeloglock CASCADE;

-- n1netails sequencies
DROP SEQUENCE IF EXISTS ntail.organization_seq;
DROP SEQUENCE IF EXISTS ntail.runbook_seq;
DROP SEQUENCE IF EXISTS ntail.tail_level_seq;
DROP SEQUENCE IF EXISTS ntail.tail_seq;
DROP SEQUENCE IF EXISTS ntail.tail_status_seq;
DROP SEQUENCE IF EXISTS ntail.tail_type_seq;
DROP SEQUENCE IF EXISTS ntail.tail_variable_seq;
DROP SEQUENCE IF EXISTS ntail.users_seq;
DROP SEQUENCE IF EXISTS ntail.note_seq;
DROP SEQUENCE IF EXISTS ntail.token_seq;
```

## Deploy Docker Image to Dockerhub
Run the `docker-deploy.sh` file to deploy the docker image to dockerhub.
