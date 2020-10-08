# Build Stage
FROM node:alpine AS builder

COPY package.json .
COPY account account
COPY WC-Modules WC-Modules

ARG NODE_ENV=production

RUN yarn install
RUN yarn workspace account build:$NODE_ENV \
&& rm -rf node_modules

# Runtime Stage
FROM node:alpine

ENV APP_HOME=/usr/app
WORKDIR $APP_HOME

COPY --from=builder package.json .
COPY --from=builder account account
COPY --from=builder WC-Modules WC-Modules

RUN yarn install --production

EXPOSE 8822
CMD ["yarn", "workspace", "account", "deploy"]
