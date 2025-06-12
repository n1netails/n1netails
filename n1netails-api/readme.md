# N1netails API

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

## Build
Build the project using the following command
```bash
mvn clean install
```

## Run
Run the project on your local computer using the following command
```bash
mvn spring-boot:run -DSPRING_PROFILE_ACTIVE=local
```

Run the project with environment variables.
- PORT (application port)
- POSTGRES_URL (postgres database url)
- POSTGRES_USERNAME (n1netails user)
- POSTGRES_PASSWORD (n1netails user password)

Example:
```bash
mvn spring-boot:run -DSPRING_PROFILE_ACTIVE=local -DPORT=9901 -DPOSTGRES_URL=jdbc:postgresql://localhost/n1netails -DPOSTGRES_USERNAME=n1netails -DPOSTGRES_PASSWORD=n1netails
```

## Deploy Docker Image to Dockerhub
Run the `docker-deploy.sh` file to deploy the docker image to dockerhub.
