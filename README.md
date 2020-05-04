## Author

```
wooka@virnect.com / WooKyungAh
ykmo@virnect.com / MoYeongKwon
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
npm -> ^5.6.0
node -> v10.15.1
```

## Build

```
$ npm install
$ npm run build
```

## Running the application

```shell script
npm run start
```

## Running the application with Docker

#### Build docker image from dockerfile
```shell script
docker build -t rm-web ./docker/Dockerfile .
```

#### Run application as docker container via docker image
```shell script
Needed SSL_ENV, NODE_ENV as Environment variables

docker run -d --name 'rm-web' -p 8886:8886 -e SSL_ENV=public NODE_ENV=production rm-web
```
