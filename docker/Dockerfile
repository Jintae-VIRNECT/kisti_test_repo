FROM openjdk:8
ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME

COPY ./build/libs/PF-Download-1.0.jar ./PF-Download-1.0.jar

EXPOSE 8086

## Command executed when container is start up.
CMD ["java", "-Xmx200M", "-jar","PF-Download-1.0.jar"]
