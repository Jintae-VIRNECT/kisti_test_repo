FROM openjdk:8-jre-alpine

ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME

COPY ./keystore.p12 ./keystore.p12
COPY ./build/libs/PF-Login-1.0.0.jar ./PF-Login-1.0.0.jar

EXPOSE 8883

## Command executed when container is start up.
CMD ["java", "-Xmx200M", "-jar","PF-Login-1.0.0.jar"]
