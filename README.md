## Author

```
sky456139@virnect.com / ChangJeongHyeon(JohnMark)
```

## Description      
```
Virnect Platform User And Authentication Server
```

## Environment

```
java -> openJDk 1.8
Gradle -> gradle 6.8 (but, gradle bundle is included this project)
```

## Build

```
$ ./gradlew clean build
```

## Running the application

```shell script
#Example: java - Dspring.profiles.active=develop -jar PF-Auth-v1.0.0.jar
java -DVIRNECT_ENV=${profile env value} -DCONFIG_SERVER=${CONFIG_SERVER_ADDRESS} -jar ${PF-UAA-v1.0.0.jar}
```

## Running the application with Docker

#### Build docker image from dockerfile
```shell script
docker build -t <imageName>:<tag> docker/${DockerfilePath} .
```

#### Run application as docker container via docker image
```shell script
docker run -d --name '<container_name>' -e 'VIRNECT_ENV=<PROFILES VALUE>' -e 'CONFIG_SERVER=<CONFIGURATION SERVER ADDRESS>' -p <host_port>:<container_port> ${docker image name}
```