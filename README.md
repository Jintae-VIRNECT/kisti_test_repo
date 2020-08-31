## Author

```
ykmo@virnect.com / MoYeongKwon
```

## Description

```
VIRNECT DashBoard Web frontend project
```

## ENV

```
NODE_ENV: local, develop, production / SSL_ENV: private, public
```

## Environment

```
@vue/cli 4.4.6
npm -> 6.14.7
node -> v12.16.1
```

## Build

```
npm install
npm run build
```

## Running the application

| Type | script | description |
|------|--------|-------------|
|Develop| npm run serve       | Run in local webpack|
|Build      | npm run build       | Build script in production|
|Run Node server with develop     |    npm run start:develop    | Run node server with NODE_ENV=develop|
|Run Node server with staging     |    npm run start:staging    | Run node server with NODE_ENV=staging|
|Run Node server with production  |    npm run start:production    | Run node server with NODE_ENV=production|
|Docker      | npm  run start       | Run node server in Docker |


## Running the application with Docker

#### Build docker image from dockerfile
```shell script
docker build -t rm-dashboard .
```

#### Run application as docker container via docker image
```shell script
docker run -p 9989:9989 --restart=always -e 'NODE_ENV=production' -d --name=rm-dashboard $aws_ecr_address/rm-dashboard:\\${GIT_TAG}
```

