# Build Stage
FROM gradle:6.8-jdk11 AS builder

# Only copy dependency-related files
COPY build.gradle settings.gradle ./

# Copy Sourcefile
COPY virnect.pfx .
COPY src src

# Build soruce file with dependnecies
RUN gradle clean build -x test --info  --no-daemon

RUN java -Djarmode=layertools -jar build/libs/*.jar extract

# Runtime Stage
FROM openjdk:11-jdk-slim

ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

COPY --from=builder ${APP_HOME}/virnect.pfx ./
COPY --from=builder ${APP_HOME}/dependencies/ ./
COPY --from=builder ${APP_HOME}/spring-boot-loader/ ./
COPY --from=builder ${APP_HOME}/snapshot-dependencies/ ./
COPY --from=builder ${APP_HOME}/application/ ./

EXPOSE 8073
CMD ["java", "org.springframework.boot.loader.JarLauncher"]