FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/n1netails-liquibase-0.2.6.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
