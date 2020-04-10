FROM node:alpine
ENV APP_HOME=/usr/app
ENV PORT=8878

WORKDIR $APP_HOME


ADD ./packages $APP_HOME/packages
ADD ./projects $APP_HOME/projects
ADD ./package.json $APP_HOME/package.json

RUN yarn install --production

EXPOSE $PORT

CMD ["yarn", "workspace", "workstation", "deploy"]







