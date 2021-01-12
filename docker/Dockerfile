# bulid stage
FROM golang:1.14 AS build
LABEL stage=builder

WORKDIR /src
COPY . .
RUN go get -u github.com/swaggo/swag/cmd/swag
RUN make build-linux

# final stage
FROM alpine

WORKDIR /
RUN apk add bash
COPY --from=build /src/build/recordserver-linux-amd64 /recordserver
COPY ./certs /certs

EXPOSE 8083

CMD ["/recordserver", "--stdout"]
