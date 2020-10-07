FROM openjdk:8-jre-slim

ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME

ARG buildDir=build/unpack

COPY ./virnect.pfx ./virnect.pfx
COPY ${buildDir}/lib BOOT-INF/lib
COPY ${buildDir}/app .

EXPOSE 8632

## Command executed when container is start up.
CMD ["java", "-XX:+UseG1GC", "-Xmx300M", "org.springframework.boot.loader.JarLauncher"]
