FROM openjdk:8
ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME

COPY ./build/libs/PF-ProcessManagement-1.0.0.jar ./PF-ProcessManagement-1.0.0.jar

EXPOSE 8090

VOLUME ["/data/process", "/usr/app/upload"]

## Command executed when container is start up.
CMD ["java", "-jar","PF-ProcessManagement-1.0.0.jar"]
