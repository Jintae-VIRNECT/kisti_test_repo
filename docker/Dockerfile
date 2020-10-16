# Build Stage
FROM node:alpine AS builder

COPY . .

ARG NODE_ENV=production

RUN npm install
RUN npm run build:$NODE_ENV \
&& rm -rf node_modules

# Runtime Stage
FROM node:alpine

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
COPY --from=builder dist dist
COPY --from=builder translate translate

RUN npm install --production

EXPOSE 8886
CMD ["npm", "run", "start"]
