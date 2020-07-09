FROM golang:1.14-buster

RUN mkdir /app
ADD . /app
WORKDIR /app
RUN go get -u github.com/swaggo/swag/cmd/swag && make  

EXPOSE 8083
CMD ["./build/recordserver", "--stdout"]
