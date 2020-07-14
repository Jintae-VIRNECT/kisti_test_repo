TARGET=recordserver
VERSION=1.0.0
BUILD=`git rev-parse HEAD`
LDFLAGS=-ldflags "-X=main.Version=${VERSION} -X=main.Build=${BUILD}"
ifndef $(GOPATH)
    GOPATH=$(shell go env GOPATH)
    export GOPATH
endif

.PHONY: clean check build docker

build: check
	@mkdir -p build
	go build -race -o build/${TARGET} main.go

check:	
	@${GOPATH}/bin/swag init
	@go vet ./...
	@go fmt ./...

clean:
	rm -fr build/${TARGET}

docker: build
	docker build --tag rm-recordserver . -f docker/Dockerfile
