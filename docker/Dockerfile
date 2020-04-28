FROM openjdk:8-jre-alpine

ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME

COPY ./upload ./upload

COPY ./build/libs/PF-Workspace-1.0.0.jar ./PF-Workspace-1.0.0.jar

EXPOSE 8082

## Command executed when container is start up.
CMD ["java", "-Xmx200M", "-jar","PF-Workspace-1.0.0.jar"]
