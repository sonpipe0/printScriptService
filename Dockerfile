FROM gradle:8.10.1-jdk21 AS builder
LABEL org.opencontainers.image.source=https://github.com/sonpipe0/printScriptService
COPY . /home/gradle/src
WORKDIR /home/gradle/src
RUN  gradle build
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/home/gradle/src/build/libs/printScriptService-0.0.1-SNAPSHOT.jar"]
