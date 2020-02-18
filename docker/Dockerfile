FROM openjdk:8
ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME

COPY ./build/libs/PF-Message-1.0.jar ./PF-Message-1.0.jar

EXPOSE 8084

## Command executed when container is start up.
CMD ["java","-Dspring.profiles.active=develop", "-jar","PF-Message-1.0.jar"]
