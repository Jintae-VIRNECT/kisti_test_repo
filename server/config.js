'use strict'
const dotenv = require('dotenv')
const fs = require('fs')
const filePath = `.env.${process.env.NODE_ENV.trim()}`
const envConfig = dotenv.parse(fs.readFileSync(filePath))
const urlsConfig = JSON.parse(fs.readFileSync('./server/urls.json'))

module.exports = {
  getAsNumber(key) {
    return Number(envConfig[key])
  },
  getAsString(key) {
    return String(envConfig[key])
  },
  getAll() {
    return envConfig
  },
  getEnv() {
    return process.env.NODE_ENV.trim()
  },
  getTargetEnv() {
    return String(envConfig['TARGET_ENV'])
  },
  getPort() {
    return String(envConfig['PORT'])
  },
  getSSLEnv() {
    return String(envConfig['SSL_ENV'])
  },
  getUrls() {
    const urls = {}
    const env = this.getTargetEnv()
    Object.keys(urlsConfig).forEach(key => {
      urls[key] = urlsConfig[key][env]
    })
    return urls
  },
}
