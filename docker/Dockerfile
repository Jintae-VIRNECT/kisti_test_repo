FROM openjdk:8-jdk-alpine

ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME

COPY ./build/libs/PF-Gateway-1.0.0.jar ./PF-Gateway-1.0.0.jar

EXPOSE 8073

## Command executed when container is start up.
CMD ["java", "-Dreactor.netty.http.server.accessLogEnabled=true", "-jar","PF-Gateway-1.0.0.jar"]
