FROM gradle:8.10.1-jdk21 AS builder
LABEL org.opencontainers.image.source=https://github.com/sonpipe0/printScriptService
COPY . /home/gradle/src
WORKDIR /home/gradle/src
RUN --mount=type=secret,id=gpr_user,env=USERNAME \
    --mount=type=secret,id=gpr_token,env=TOKEN \
    gradle build --refresh-dependencies
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "echo $USERNAME:$TOKEN should be empty"]
ENTRYPOINT ["java", "-jar", "/home/gradle/src/build/libs/PrintScriptService-0.0.1-SNAPSHOT.jar"]
