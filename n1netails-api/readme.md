# N1ne Tails API

## Requirements
- Java 17
- PostgreSQL

Set up postgres database create database `n1netails`
Postgres local user 

username: postgres \
password: postgres

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