FROM openjdk:8-jre-alpine

ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME

COPY ./build/libs/PF-ContentManagement-1.0.0.jar ./PF-ContentManagement-1.0.0.jar

EXPOSE 8078

VOLUME ["/data/content", "/usr/app/upload"]

## Command executed when container is start up.
CMD ["java", "-Xmx500M", "-jar","PF-ContentManagement-1.0.0.jar"]
