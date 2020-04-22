FROM openjdk:8
ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME

COPY ./build/libs/PF-Workspace-1.0.0.jar ./PF-Workspace-1.0.0.jar

ENV JAVA_OPTS="-XX:MaxMetaspaceSize=512m -XX:MetaspaceSize=256m"

EXPOSE 8082

## Command executed when container is start up.
CMD ["java", "-jar","PF-Workspace-1.0.0.jar"]
