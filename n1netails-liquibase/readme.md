# N1ne Tails Liquibase

## Requirements
- Java 17
- PostgreSQL

Set up postgres database create database `n1netails`
Postgres local user

username: postgres \
password: postgres

## Create
Create database `n1netails` in postgres pgAdmin 4 if none exists or use the following command to create database:
```sql
CREATE DATABASE "n1netails"
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LOCALE_PROVIDER = 'libc'
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;
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
- POSTGRES_USERNAME (postgres user)
- POSTGRES_PASSWORD (postgres user password)

Example:
```bash
mvn spring-boot:run -DSPRING_PROFILE_ACTIVE=local -DPOSTGRES_URL=jdbc:postgresql://localhost/n1netails -DPOSTGRES_USERNAME=postres -DPOSTGRES_PASSWORD=postgres
```

## Liquibase Maven Commands

Rollback (example of rolling back 2 times)
```bash
mvn liquibase:rollback -Dliquibase.rollbackCount=2
```
