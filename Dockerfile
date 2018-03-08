FROM openjdk:9.0.1-jdk-slim
VOLUME /tmp
ARG JAR_FILE
ADD ${JAR_FILE} hello-spring-boot-docker-0.0.1-SNAPSHOT.jar
ENTRYPOINT [“java”,”-Djava.security.egd=file:/dev/./urandom”,”-jar”,”/hello-spring-boot-docker-0.0.1-SNAPSHOT.jar”]