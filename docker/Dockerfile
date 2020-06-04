FROM node:alpine
ENV APP_HOME=/usr/app
ENV PORT=8878

WORKDIR $APP_HOME


ADD ./workstation $APP_HOME/workstation
ADD ./WC-Modules $APP_HOME/WC-Modules
ADD ./package.json $APP_HOME/package.json

RUN yarn install --production

EXPOSE $PORT

CMD ["yarn", "workspace", "workstation", "deploy"]







