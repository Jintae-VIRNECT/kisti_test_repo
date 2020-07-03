TARGET=recordserver
VERSION=1.0.0
BUILD=`git rev-parse HEAD`
LDFLAGS=-ldflags "-X=main.Version=$(VERSION) -X=main.Build=$(BUILD)"

build: 
    #check
	@mkdir -p build
	go build -race -o build/${TARGET} main.go

check:	
	#@swag init
	@go vet ./...
	@go fmt ./...

clean:
	rm -fr build/${TARGET}

docker: build
	docker build --tag rm-recordserver . -f docker/Dockerfile
