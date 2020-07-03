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

### run
```bash
docker run -it --rm -p 8083:8083 -v /var/run/docker.sock:/var/run/docker.sock --name recordserver rm-recordserver
```