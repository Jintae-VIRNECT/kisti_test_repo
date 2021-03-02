# Build Stage
FROM openjdk:8 AS builder

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

COPY service-kms service-kms
COPY service-client service-client
COPY service-java-client service-java-client

COPY service-data service-data
COPY service-server service-server

COPY virnect.pfx .

RUN chmod +x ./gradlew
RUN ./gradlew clean
RUN ./gradlew :service-server:build -x test

# Runtime Stage
FROM openjdk:8-jre-alpine

ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

COPY --from=builder virnect.pfx .
COPY --from=builder service-server/build/libs/RM-Service-*.jar ./RM-Service-2.0.jar

EXPOSE 8000
CMD ["java", "-XX:+UseG1GC", "-Xmx400M", "-jar", "RM-Service-2.0.jar"]
