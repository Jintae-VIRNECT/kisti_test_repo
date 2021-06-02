# Build Stage #1 build depency
FROM gradle:6.8-jdk11 AS build_dependency
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

# Only copy dependency-related files
COPY build.gradle settings.gradle ./

RUN gradle build --info --no-daemon  > /dev/null 2>&1 || true


FROM gradle:6.8-jdk11 AS builder
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

# Copy gradle cache 
COPY --from=build_dependency /home/gradle/.gradle /home/gradle
COPY --from=build_dependency ${APP_HOME}/.gradle ${APP_HOME}

# Only copy dependency-related files
COPY build.gradle settings.gradle ./

# Copy Sourcefile
COPY virnect.pfx .
COPY src src

# build soruce file with dependnecies
RUN gradle clean build -x test --info  --no-daemon

# Runtime Stage
FROM openjdk:11-jdk-slim

# Label
LABEL APPLICATION="PF-License"

ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
ARG buildDir=$APP_HOME/build/unpack

COPY --from=builder ${APP_HOME}/virnect.pfx .
COPY --from=builder ${buildDir}/lib BOOT-INF/lib
COPY --from=builder ${buildDir}/app .

EXPOSE 8632
CMD ["java", "-Xms400M", "-Xmx400M", "org.springframework.boot.loader.JarLauncher"]