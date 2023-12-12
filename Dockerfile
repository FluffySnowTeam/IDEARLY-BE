FROM openjdk:17-jdk
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} idearly-api.jar
ENTRYPOINT ["java", "-jar", "/idearly-api.jar"]