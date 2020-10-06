FROM openjdk:8-jre-alpine

ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME

COPY ./virnect.pfx ./virnect.pfx
COPY ./build/libs/PF-ProcessManagement-1.0.0.jar ./PF-ProcessManagement-1.0.0.jar

EXPOSE 8079

## Command executed when container is start up.
CMD ["java", "-XX:+UseG1GC", "-Xmx500M", "-jar","PF-ProcessManagement-1.0.0.jar"]
