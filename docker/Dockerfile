# Build Stage
FROM node:alpine AS builder

COPY . .

ARG NODE_ENV=production

RUN yarn install
RUN yarn build \
&& rm -rf node_modules

# Runtime Stage
FROM node:alpine

ENV APP_HOME=/usr/app
WORKDIR $APP_HOME

COPY --from=builder cert cert
COPY --from=builder configs configs
COPY --from=builder route.js .
COPY --from=builder server.js .
COPY --from=builder package.json .
COPY --from=builder dist dist

RUN yarn install --production

EXPOSE 8883
CMD ["yarn", "start"]