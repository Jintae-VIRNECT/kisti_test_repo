## Author

 

```
wooka@virnect.com / WooKyungAh
```

 

## Description

 

```
VIRNECT Remote Web frontend project
```

 

## Environment

 

```
npm -> ^5.6.0
node -> v10.15.1
```

 

## Build

 

```
$ npm install
$ npm run build:develop
```

 

## Running the application

 

```shell script
npm run start:develop
```

 

## Running the application with Docker

 

#### Build docker image from dockerfile
```shell script
docker build -t remoteweb:develop ./Dockerfile.develop .
```

 

#### Run application as docker container via docker image
```shell script
docker run -d --name 'remoteweb-develop' -p 8886:8886 remoteweb:develop
```
## Running the application with Package
1. run build files
```
npm run start:develop
```
 