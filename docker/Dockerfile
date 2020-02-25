FROM node:alpine
ENV APP_HOME=/usr/app
ENV PORT=8886

WORKDIR $APP_HOME

ADD . /$APP_HOME

RUN npm install --production

EXPOSE ${PORT}

CMD ["npm", "run", "start"]

