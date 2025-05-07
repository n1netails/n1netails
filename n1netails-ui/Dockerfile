FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/n1netails-ui-0.0.3.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
