FROM openjdk:8-jre-slim
ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME

COPY ./virnect.pfx ./virnect.pfx

ARG buildDir=build/unpack

COPY ${buildDir}/lib BOOT-INF/lib
COPY ${buildDir}/app .

EXPOSE 8761

## Command executed when container is start up.
CMD ["java", "-XX:+UseG1GC", "-Xmx200M", "org.springframework.boot.loader.JarLauncher"]
