# Build Stage
FROM node:12-alpine AS builder

COPY . .

ARG NODE_ENV=production

RUN yarn install --production=false
RUN yarn workspace account build:$NODE_ENV \
&& rm -rf node_modules

# Runtime Stage
FROM node:12-alpine

ENV APP_HOME=/usr/app
WORKDIR $APP_HOME

COPY --from=builder package.json .
COPY --from=builder account account

RUN yarn install --production=true

EXPOSE 8822
CMD ["yarn", "workspace", "account", "deploy"]
