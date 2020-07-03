FROM golang:1.14-buster

COPY ./config.ini ./config.ini
COPY ./build/recordserver ./recordserver

EXPOSE 8083

CMD ["./recordserver", "--stdout"]
