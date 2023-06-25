FROM adoptopenjdk:11-jdk-hotspot
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]