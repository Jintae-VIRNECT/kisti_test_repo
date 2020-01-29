const dotenv = require('dotenv')
const fs = require('fs')
const filePath = `.env.${process.env.NODE_ENV.trim()}`
const envConfig = dotenv.parse(fs.readFileSync(filePath))

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
    return process.env.NODE_ENV
  },
  getPort() {
    return String(envConfig['PORT'])
  },
  getSSLEnv() {
    return process.env.SSL_ENV
  },
}
