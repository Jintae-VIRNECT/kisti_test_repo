# Build Stage
FROM node:12-alpine AS builder

COPY . .

ARG NODE_ENV=production

RUN npm --production=false install
RUN npm run build \
&& rm -rf node_modules

# Runtime Stage
FROM node:12-alpine

ENV APP_HOME=/usr/app
WORKDIR $APP_HOME

COPY --from=builder record record
COPY --from=builder server server
COPY --from=builder ssl ssl
COPY --from=builder static static
COPY --from=builder route.js .
COPY --from=builder server.js .
COPY --from=builder package.json .
COPY --from=builder package-lock.json .
COPY --from=builder .npmrc .
COPY --from=builder dist dist
COPY --from=builder translate translate

RUN npm install --production

EXPOSE 8886
CMD ["npm", "run", "start"]
