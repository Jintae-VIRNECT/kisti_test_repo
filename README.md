# RM-RecordServer

## Prerequisites

### go
Download link: https://golang.org/dl/
```bash
wget https://dl.google.com/go/go1.14.4.linux-amd64.tar.gz
tar -xvf go1.12.2.linux-amd64.tar.gz
mv go /usr/local
```
`~/.bashrc` 에 추가
```bash
export GOPATH=$HOME/go
export PATH=$PATH:/usr/local/go/bin:$GOPATH/bin
```

### swagger
Download Swag for Go by using:
```bash
go get -u github.com/swaggo/swag/cmd/swag
```

## Build
```bash
make
```

## Run
```
./build/recordserver
```

## Docker

### Build
```bash
make docker
```
혹은
```bash
docker build --tag virnect/remote-recordserver . -f docker/Dockerfile
```

### Run
다음과 같이 volume이 마운트 되어야 한다.
- recording 파일이 저장될 호스트의 디렉토리 (설정 파일의 내용과 같아야 한다.)
- 호스트의 docker.sock
- 설정 파일을 컨테이너 외부에서 관리하려면 설정 파일 위치

Linux
```bash
docker run -it --rm -p 8083:8083 \
    -v /var/run/docker.sock:/var/run/docker.sock \
    -v /Users/VIRNECT/RM-RecordServer/recordings:/recordings \
    -v /Users/VIRNECT/RM-RecordServer/config.dev.ini:/config.ini \
    --name remote-recordserver virnect/remote-recordserver
```

Windows
```
docker run -it --rm -p 8083:8083 `
    -v /var/run/docker.sock:/var/run/docker.sock `
    -v //c/Users/VIRNECT/RM-RecordServer/recordings:/recordings `
    -v //c/Users/VIRNECT/RM-RecordServer/config.win.ini:/config.ini `
    --name remote-recordserver virnect/remote-recordserver
```

## Custom Layout Server
recorder container는 내부적으로 chrome를 사용한다. 따라서 녹화를 위한 layout 페이지가 필요하고, 이를 위해서 web server가 있어야 한다.
`http-server`를 사용해서 간단하게 custom layout server를 만들 수 있다.

### `http-server` 설치

```bash
npm install -g http-server
```

### custom layout server 실행

```bash
http-server -p 9000 -S -C custom-layout/cert/cert.pem -K custom-layout/cert/key.pem custom-layout/public/
```

docker
```bash
cd custom-layout
docker build --tag httpserver .
docker run -it --rm -p 9000:9000 --name httpserver httpserver
```

### `layoutURL` 설정

`config.ini`에서 `record.layoutURL`의 값을 위에서 실행한 서버 주소로 변경한다.

### `openvidu-browser`

- openvidu-browser-2.14.0.min.js: openvidu에서 제공하는 웹 클라이언트 라이브러리
- openvidu-browser-dev.js: openvidu-server 수정에 대응된 버전
