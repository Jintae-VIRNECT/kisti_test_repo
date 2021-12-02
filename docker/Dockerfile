# Build Stage
FROM node:12-alpine AS builder

COPY . .

ARG NODE_ENV=production
ARG NPM_TOKEN
COPY .npmrc .npmrc

RUN npm install --production=false
RUN npm run build \
&& rm -rf node_modules
RUN rm -f .npmrc

# Runtime Stage
FROM node:12-alpine

ENV APP_HOME=/usr/app
WORKDIR $APP_HOME

COPY --from=builder cert cert
COPY --from=builder configs configs
COPY --from=builder meta.js .
COPY --from=builder route.js .
COPY --from=builder server.js .
COPY --from=builder package.json .
COPY --from=builder dist dist
COPY .npmrc .npmrc

RUN npm install --production=true

EXPOSE 8883
CMD ["npm", "start"]
