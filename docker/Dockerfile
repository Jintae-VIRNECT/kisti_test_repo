FROM openjdk:8
ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME

COPY ./build/libs/PF-Eureka-1.0.0.jar ./PF-Eureka-1.0.0.jar

EXPOSE 8761

## Command executed when container is start up.
CMD ["java","-Dspring.profiles.active=develop", "-jar","PF-Eureka-1.0.0.jar"]
