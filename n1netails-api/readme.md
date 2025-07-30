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
mvn spring-boot:run -DSPRING_PROFILE_ACTIVE=local
```

Run the project with environment variables.
- PORT (application port defaults to 9901)
- POSTGRES_URL (postgres database url defaults to jdbc:postgresql://localhost/n1netails)
- POSTGRES_USERNAME (n1netails user defaults to n1netails)
- POSTGRES_PASSWORD (n1netails user password defaults to n1netails)
- GEMINI_ENABLED (defaults to false)
- GEMINI_API_KEY (create your own gemini api key)
- GEMINI_API_URL (defaults to https://generativelanguage.googleapis.com)
- OPENAI_ENABLED (defaults to false)
- OPENAI_API_KEY (create your own openai api key)
- OPENAI_API_URL (defaults to https://api.openai.com)
- N1NETAILS_PASSKEY_RELYING_PARTY_ID (defaults to localhost)
- N1NETAILS_PASSKEY_ORIGINS (defaults to http://localhost:8080,http://localhost:9900,http://localhost:9901,http://localhost:4200)
- GITHUB_OAUTH2_ENABLED (defaults to false)
- AUTH_OAUTH2_REDIRECT_SUCCESS (defaults to http://localhost:4200/#/oauth2/success?token=)
- GITHUB_CLIENT_ID (create your own github client id via https://github.com/settings/developers)
- GITHUB_CLIENT_SECRET (create your own github client secret via https://github.com/settings/developers)

Example:
```bash
mvn spring-boot:run 
-DSPRING_PROFILE_ACTIVE=local 
-DPORT=9901 
-DPOSTGRES_URL=jdbc:postgresql://localhost/n1netails 
-DPOSTGRES_USERNAME=n1netails 
-DPOSTGRES_PASSWORD=n1netails 
-DN1NETAILS_PASSKEY_RELYING_PARTY_ID=localhost 
-DN1NETAILS_PASSKEY_ORIGINS=http://localhost:8080,http://localhost:9900,http://localhost:9901,http://localhost:4200
```

## Deploy Docker Image to Dockerhub
Run the `docker-deploy.sh` file to deploy the docker image to dockerhub.
