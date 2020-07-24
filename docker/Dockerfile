FROM node:alpine
ENV APP_HOME=/usr/app
ENV PORT=8833

WORKDIR $APP_HOME


ADD ./download $APP_HOME/download
ADD ./WC-Modules $APP_HOME/WC-Modules
ADD ./package.json $APP_HOME/package.json

RUN yarn install --production

EXPOSE $PORT

CMD ["yarn", "workspace", "download", "deploy"]