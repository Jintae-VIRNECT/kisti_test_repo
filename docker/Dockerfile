FROM openjdk:8-jre-alpine

ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME

COPY ./virnect.pfx ./virnect.pfx
COPY ./build/libs/PF-Message-1.0.jar ./PF-Message-1.0.jar

EXPOSE 8084

## Command executed when container is start up.
CMD ["java", "-XX:+UseG1GC", "-Xmx200M", "-jar", "PF-Message-1.0.jar"]
