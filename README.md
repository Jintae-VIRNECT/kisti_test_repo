## Author

```
sky456139@virnect.com / ChangJeongHyeon(JohnMark)
```

## Description      
```
Platform Micro Service Discovery Server
```

## Environment

```
java -> openJDk 1.8 ^
Gradle -> gradle 6.* (but, gradle bundle is included this project)
```

## Build

```
$ ./gradlew clean build
```

## Running the application

```shell script
#Example: java - Dspring.profiles.active=develop -jar SMIC_CUSTOM-v1.0.jar
java -Dspring.profiles.active=${profile env value} -jar ${SMIC_CUSTOM-v1.0.jar}
```

## Running the application with Docker

#### Build docker image from dockerfile
```shell script
docker build -t <imageName>:<tag> ${DockerfilePath} .
```

#### Run application as docker container via docker image
```shell script
docker run -d --name '<container_name>' -p <host_port>:<container_port> ${docker image name}
```
## Running the application with Package
1. add follow script into build.gradle
```
launch4j { 
 outfile = '파일명.exe'
 jar = 'jar파일 경로.jar파일'
 headerType ='console'
}
```
2. run follow script
```shell script
./gradlew launch4j:createExe
```
