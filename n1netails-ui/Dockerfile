FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/n1netails-ui-0.2.8.jar app.jar
EXPOSE 9900
ENTRYPOINT ["java", "-jar", "app.jar"]
