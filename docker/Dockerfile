# Build Stage
FROM node:14-alpine AS builder

COPY . .

ARG NODE_ENV=production

RUN yarn install --production=false
RUN yarn run build \
&& rm -rf node_modules

# Runtime Stage
FROM node:14-alpine

ENV APP_HOME=/usr/app
WORKDIR $APP_HOME

COPY --from=builder ssl ssl
COPY --from=builder server server
COPY --from=builder route.js .
COPY --from=builder server.js .
COPY --from=builder package.json .
COPY --from=builder .npmrc .
COPY --from=builder dist dist
COPY --from=builder yarn.lock .

RUN yarn install --production=true

EXPOSE 9989
CMD ["yarn", "run", "start"]
