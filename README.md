## Author

```
ykmo@virnect.com / MoYeongKwon
```

## Description

```
VIRNECT DashBoard Web frontend project
```

## ENV


| Value | Option 
|-------------|---------------------|
| NODE_ENV    | develop, production | 
| SSL_ENV     | private, public     | 
| VIRNECT_ENV | develop, staging, production, onpremise |
| CONFIG_SERVER | http://192.168.6.3:6383, https://stgconfig.virnect.com, https://config.virnect.com|


## Environment

```
@vue/cli 4.5.7
npm -> 6.14.8
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
|Develop    | npm run serve         | Run in local webpack |
|Build      | npm run build         | Build script in production|
|Run Node server with develop       |    npm run start:develop       
|Run Node server with staging       |    npm run start:staging       
|Run Node server with onpremise     |    npm run start:op            
|Run Node server with production    |    npm run start:production    
|Docker      | npm  run start       | Run node server in Docker |


## Running the application with Docker

#### Build docker image from dockerfile
```shell script
docker build -t rm-dashboard .
```
