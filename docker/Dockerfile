FROM node:alpine
ENV APP_HOME=/usr/app
ENV PORT=8822

WORKDIR $APP_HOME


ADD ./account $APP_HOME/account
ADD ./WC-Modules $APP_HOME/WC-Modules
ADD ./package.json $APP_HOME/package.json

RUN yarn install --production

EXPOSE $PORT

CMD ["yarn", "workspace", "account", "deploy"]
