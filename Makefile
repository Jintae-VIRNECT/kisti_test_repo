TARGET=recordserver
BUILD=`git rev-parse HEAD`
LDFLAGS=-ldflags "-X=main.Build=${BUILD}"
DOCKER_TAG=virnect/remote-recordserver
SRCS=main.go version.go config.go
BUILD_OPT=-race
ifndef $(GOPATH)
    GOPATH=$(shell go env GOPATH)
    export GOPATH
endif

.PHONY: clean check build build-linux docker swag

build: check
	@mkdir -p build
	go build ${BUILD_OPT} ${LDFLAGS} -o build/${TARGET} ${SRCS}

build-linux: check
	@mkdir -p build
	CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build ${LDFLAGS} -o build/${TARGET}-linux-amd64 ${SRCS}

swag:
	@${GOPATH}/bin/swag init

check: test swag
	@go vet ./...
	@go fmt ./...

test:
	go test ./...

clean:
	rm -fr build/${TARGET}*

docker:
	docker build --rm --tag ${DOCKER_TAG} . -f docker/Dockerfile
	docker image prune -f --filter label=stage=builder
