FROM node:alpine

WORKDIR /

ADD . /

RUN npm install --production

ENV PORT=8887

ENV NODE_ENV=production

ENV SSL_ENV=public

ENV PATH /usr/local/bin:$PATH

EXPOSE ${PORT}

CMD ["npm", "run", "start"]

