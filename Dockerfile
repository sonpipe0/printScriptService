# syntax=docker/dockerfile:1
FROM gradle:8.10.1-jdk21 AS builder
LABEL org.opencontainers.image.source=https://github.com/sonpipe0/printScriptService
COPY . /home/gradle/src
WORKDIR /home/gradle/src
RUN --mount=type=secret,id=gpr_user,env=USERNAME,required \
    --mount=type=secret,id=gpr_token,env=TOKEN,required \
    gradle build

FROM openjdk:21-jdk
COPY --from=builder /home/gradle/src/build/libs/PrintScriptService-0.0.1-SNAPSHOT.jar /app/PrintScriptService.jar
COPY newrelic-java/newrelic /app/newrelic/
ARG NEW_RELIC_LICENSE_KEY
ENV NEW_RELIC_APP_NAME="printscript service"
ENV NEW_RELIC_LICENSE_KEY=${NEW_RELIC_LICENSE_KEY}
EXPOSE 8080
ENTRYPOINT ["java", "-javaagent:/app/newrelic/newrelic.jar", "-jar", "/app/PrintScriptService.jar"]
