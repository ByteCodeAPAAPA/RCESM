FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/RCES-*.jar app.jar

COPY src/main/resources/application.properties application.properties
COPY src/main/resources/application-docker.properties application-docker.properties

EXPOSE 2520

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=file:/app/application-docker.properties"]