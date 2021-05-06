## Author

```
hoon@virnect.com / Kim Kyung Hoon (Hoon)
```

## Description      
```
Remote API Service Server
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

## Build jar 

```
$ ./gradlew bootJar (@deprecated)

#below command is deprecated
$ ./gradlew :service-server:clean :service-server:build -x test -Dspring.profiles=local (@deprecated)

#currnet command is activated
$ ./gradlew :service-server:clean :service-server:build -x test -DVIRNECT_ENV=local
```

## Running the application

```shell script
#below command is deprecated
java -Dspring.profiles.active=${profile env value} -jar ${RM-Service-v2.0.0.jar} (@deprecated)

#currnet command is activated
java -DVIRNECT_ENV=${VIRNECT_ENV} -jar ${RM-Service-v2.0.0.jar}
```

## Running the application with jar

```shell script
java -jar ${RM-Service-v2.0.0.jar}
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

## Server Application logging

