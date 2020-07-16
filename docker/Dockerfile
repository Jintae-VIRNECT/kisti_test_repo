FROM openjdk:8-jre-alpine
ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME

ARG BUILD=build/unpack

COPY ${BUILD}/app .
COPY ${BUILD}/lib/ BOOT-INF/lib/

EXPOSE 8761

## Command executed when container is start up.
CMD ["java",  "-Xmx200M", "org.springframework.boot.loader.JarLauncher"]
