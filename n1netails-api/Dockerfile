FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/n1netails-api-0.1.20.jar app.jar
EXPOSE 9901
ENTRYPOINT ["java", "-jar", "app.jar"]
