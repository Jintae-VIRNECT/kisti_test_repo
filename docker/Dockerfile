# Build Stage
FROM node:alpine AS builder

COPY package.json .
COPY download download
COPY WC-Modules WC-Modules

ARG NODE_ENV=production

RUN yarn install
RUN yarn workspace download build:$NODE_ENV \
&& rm -rf node_modules

# Runtime Stage
FROM node:alpine

ENV APP_HOME=/usr/app
WORKDIR $APP_HOME

COPY --from=builder package.json .
COPY --from=builder download download

RUN yarn install --production

EXPOSE 8833
CMD ["yarn", "workspace", "download", "deploy"]