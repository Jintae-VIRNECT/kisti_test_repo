FROM node:12

WORKDIR /test

COPY package*.json ./
COPY run.js ./

RUN npm install

CMD [ "node", "./run.js" ]
