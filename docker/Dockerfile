FROM node:alpine

ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME

ADD ./ $APP_HOME
ADD ./WC-Modules $APP_HOME/WC-Modules
ADD ./package.json $APP_HOME/package.json

RUN yarn install

EXPOSE 8883

CMD ["yarn", "start"]
