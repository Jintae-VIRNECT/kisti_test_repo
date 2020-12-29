# Build Stage
FROM node:12-alpine AS builder

COPY . .

ARG NODE_ENV=production

RUN yarn install --production=false
RUN yarn build:$NODE_ENV \
&& rm -rf node_modules

# Runtime Stage
FROM node:12-alpine

ENV APP_HOME=/usr/app
WORKDIR $APP_HOME

COPY --from=builder . .

RUN yarn install --production=true

EXPOSE 8833
CMD ["yarn", "deploy"]