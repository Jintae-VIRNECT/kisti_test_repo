FROM openjdk:8
ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME

COPY ./build/libs/PF-Login-1.0.0.jar ./PF-Login-1.0.0.jar

EXPOSE 8883

## Command executed when container is start up.
CMD ["java", "-jar","PF-Login-1.0.0.jar"]
