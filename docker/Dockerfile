# Build Stage
FROM openjdk:8 AS builder

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
COPY virnect.pfx .

RUN chmod +x ./gradlew
RUN ./gradlew cleanQuerydslSourcesDir
RUN ./gradlew build -x test

# Runtime Stage
FROM openjdk:8-jre-alpine

ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
ARG buildDir=build/unpack

COPY --from=builder virnect.pfx .
COPY --from=builder ${buildDir}/lib BOOT-INF/lib
COPY --from=builder ${buildDir}/app .
COPY tmp $APP_HOME/tmp

EXPOSE 8086
CMD ["java", "-Xms900M", "-Xmx900M", "org.springframework.boot.loader.JarLauncher"]
