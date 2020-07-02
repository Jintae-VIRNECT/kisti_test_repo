TARGET=recordserver
VERSION=1.0.0
BUILD=`git rev-parse HEAD`
LDFLAGS=-ldflags "-X=main.Version=$(VERSION) -X=main.Build=$(BUILD)"

build: check
	go build -race -o ${TARGET} main.go

check:
	@${GOPATH}/bin/swag init
	@go vet ./...
	@go fmt ./...

clean:
	rm -fr ${TARGET}
