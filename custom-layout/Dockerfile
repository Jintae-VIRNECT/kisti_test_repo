FROM node:12-alpine

WORKDIR /
COPY cert/* cert/
COPY public public/
RUN npm install -g http-server

EXPOSE 9000
CMD ["http-server", "-p", "9000", "-S", "-C", "/cert/cert.pem", "-K", "/cert/key.pem", "/public/"]
