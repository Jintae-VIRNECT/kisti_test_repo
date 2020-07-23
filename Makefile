TARGET=recordserver
VERSION=2.0.0
BUILD=`git rev-parse HEAD`
LDFLAGS=-ldflags "-X=main.Version=${VERSION} -X=main.Build=${BUILD}"
DOCKER_TAG=virnect/remote-recordserver
BUILD_OPT=-race
ifndef $(GOPATH)
    GOPATH=$(shell go env GOPATH)
    export GOPATH
endif

.PHONY: clean check build build-linux docker swag

build: check
	@mkdir -p build
	go build ${BUILD_OPT} ${LDFLAGS} -o build/${TARGET} main.go

build-linux: check
	@mkdir -p build
	CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build ${LDFLAGS} -o build/${TARGET}-linux-amd64 main.go

swag:
	@${GOPATH}/bin/swag init

check: swag
	@go vet ./...
	@go fmt ./...

clean:
	rm -fr build/${TARGET}*

docker:
	docker build --rm --tag ${DOCKER_TAG} . -f docker/Dockerfile
	docker image prune -f --filter label=stage=builder
