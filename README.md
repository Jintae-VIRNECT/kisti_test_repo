## Author

```
ykmo@virnect.com / MoYeongKwon
yunze89@virnect.com / HongYunsuk
```

## Description

```
VIRNECT Remote Web frontend project
```


## ENV

```
NODE_ENV: local, develop, production / SSL_ENV: private, public
```

## Environment

```
yarn -> 1.22.4
node -> 14.17.0
```

## Build

```
package.json 참조
```

## Running the application

```shell script
package.json 참조
```

## Running the application with Docker

#### Build docker image from dockerfile
```shell script
docker build -t rm-web ./docker/Dockerfile .
```

#### Run application as docker container via docker image
```shell script
Needed SSL_ENV, NODE_ENV as Environment variables

docker run -d --name 'rm-web' -p 8886:8886 -e NODE_ENV=production rm-web
```
