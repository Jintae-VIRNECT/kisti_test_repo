FROM openjdk:8-jre-alpine

ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME

#COPY ./build/service-server/libs/RM-Service-*.jar ./RM-Service-2.0.0.jar
COPY ./virnect.pfx ./virnect.pfxS
COPY ./service-server/build/libs/RM-Service-*.jar ./RM-Service-2.1.0.jar

EXPOSE 8000

## Command executed when container is start up.
CMD ["java", "-XX:+UseG1GC", "-Xmx300M", "-jar", "RM-Service-2.1.0.jar"]
