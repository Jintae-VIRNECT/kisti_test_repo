# Build Stage
FROM node:alpine AS builder

COPY . .

ARG NODE_ENV=production

RUN npm --production=false install
RUN npm run build \
&& rm -rf node_modules

# Runtime Stage
FROM node:alpine

ENV APP_HOME=/usr/app
WORKDIR $APP_HOME

COPY --from=builder ssl ssl
COPY --from=builder server server
COPY --from=builder route.js .
COPY --from=builder server.js .
COPY --from=builder package.json .
COPY --from=builder dist dist

RUN npm --production=true install 

EXPOSE 9989
CMD ["npm", "run", "start"]
