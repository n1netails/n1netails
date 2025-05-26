FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/n1netails-liquibase-0.1.7.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
