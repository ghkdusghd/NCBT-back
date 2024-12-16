FROM openjdk:17-slim

USER root
RUN apt-get update && apt-get install -y default-mysql-client

ARG JAR_FILE=build/libs/backend-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]