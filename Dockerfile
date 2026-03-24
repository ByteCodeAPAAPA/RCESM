# syntax=docker/dockerfile:1.7

FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /workspace

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src

RUN chmod +x gradlew && \
    ./gradlew --no-daemon clean bootWar -x test

FROM eclipse-temurin:17-jre-jammy AS runtime
WORKDIR /app

ENV JAVA_OPTS=""

COPY --from=build /workspace/build/libs/RCES.war /app/app.war

EXPOSE 2520

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.war"]
