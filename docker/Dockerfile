# Build Stage
FROM node:alpine AS builder

COPY . .

ARG NODE_ENV=production

RUN yarn install --production=false
RUN yarn workspace workstation build:$NODE_ENV \
&& rm -rf node_modules

# Runtime Stage
FROM node:alpine

ENV APP_HOME=/usr/app
WORKDIR $APP_HOME

COPY --from=builder package.json .
COPY --from=builder workstation workstation

RUN yarn install --production=true

EXPOSE 8878
CMD ["yarn", "workspace", "workstation", "deploy"]




