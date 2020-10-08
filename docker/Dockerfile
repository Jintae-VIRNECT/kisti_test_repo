# Build Stage
FROM node:alpine AS builder

COPY package.json .
COPY workstation workstation
COPY WC-Modules WC-Modules

ARG NODE_ENV=production

RUN yarn install
RUN yarn workspace workstation build:$NODE_ENV \
&& rm -rf node_modules

# Runtime Stage
FROM node:alpine

ENV APP_HOME=/usr/app
WORKDIR $APP_HOME

COPY --from=builder package.json .
COPY --from=builder workstation workstation

RUN yarn install --production

EXPOSE 8878
CMD ["yarn", "workspace", "workstation", "deploy"]




