FROM openjdk:8
ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME

COPY ./build/libs/PF-Gateway-1.0.0.jar ./PF-Gateway-1.0.0.jar

EXPOSE 8073

## Command executed when container is start up.
CMD ["java","-Dspring.profiles.active=develop", "-jar","PF-Gateway-1.0.0.jar"]
