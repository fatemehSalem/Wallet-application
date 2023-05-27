#FROM openjdk:8-jdk-alpine
#FROM openjdk:21-slim-buster
#RUN addgroup -S spring && adduser -S spring -G spring
FROM adoptopenjdk:11-jdk-hotspot
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]