FROM openjdk:8-jre-alpine

ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME

COPY ./virnect.pfx ./virnect.pfx
COPY ./build/libs/PF-Gateway-1.0.0.jar ./PF-Gateway-1.0.0.jar

EXPOSE 8073

## Command executed when container is start up.
CMD ["java", "-XX:+UseG1GC", "-Xmx400M","-Dreactor.netty.http.server.accessLogEnabled=true", "-jar","PF-Gateway-1.0.0.jar"]
