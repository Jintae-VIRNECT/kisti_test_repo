# Build Stage
FROM gradle:6.8-jdk8 AS builder
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

# Only copy dependency-related files
COPY build.gradle settings.gradle ./
COPY virnect.pfx .

COPY service-kms service-kms
COPY service-client service-client
COPY service-java-client service-java-client

COPY service-data service-data
COPY service-server service-server

# build soruce file with dependnecies
RUN gradle clean
RUN gradle :service-server:build -x test --stacktrace


# Runtime Stage
FROM openjdk:8-jre-alpine

ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME


COPY --from=builder ${APP_HOME}/virnect.pfx .
COPY --from=builder ${APP_HOME}/service-server/build/libs/RM-Service-2.3.jar ./RM-Service-2.3.jar

EXPOSE 8000
CMD ["java", "-XX:+UseG1GC", "-Xmx400M", "-jar", "RM-Service-2.3.jar"]