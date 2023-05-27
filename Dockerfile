FROM openjdk:17-jdk-slim
LABEL authors="yoonsoobin"

VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]