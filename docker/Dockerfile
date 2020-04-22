FROM openjdk:8-jdk-alpine

ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME

COPY ./keystore.p12 ./keystore.p12
COPY ./build/libs/PF-Gateway-1.0.0.jar ./PF-Gateway-1.0.0.jar

EXPOSE 8073

ENV JAVA_OPTS="-XX:MaxMetaspaceSize=512m -XX:MetaspaceSize=256m"

## Command executed when container is start up.
CMD ["java", "-Dreactor.netty.http.server.accessLogEnabled=true", "-jar","PF-Gateway-1.0.0.jar"]
