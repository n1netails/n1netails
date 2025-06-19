# N1netails UI

## Requirements
- Java 17
- node.js
- angular/cli

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
- PORT (application port defaults to 9900)
- API_BASE_URL (n1netails-api url defaults to http://localhost:9901)
- GEMINI_ENABLED (enable gemini defaults to false)
- OPENAI_ENABLED (enable openai defaults to false)

Example:
```bash
mvn spring-boot:run -DSPRING_PROFILE_ACTIVE=local -DPORT=9900 -DAPI_BASE_URL=http://localhost:9901
```

## Run Angular Application Locally
`cd` into `src/main/typescript`
Run the following command to start the angular application
```bash
npm run start
```
Read more here: [Angular Readme](src/main/typescript/README.md)

## Deploy Docker Image to Dockerhub
Run the `docker-deploy.sh` file to deploy the docker image to dockerhub.
