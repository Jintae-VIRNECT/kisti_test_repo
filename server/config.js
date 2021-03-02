'use strict'
const env = process.env.VIRNECT_ENV.trim()
const configServer = process.env.CONFIG_SERVER.trim()

const axios = require('axios')

let envConfig = {}
let settingConfig = {}
let urlConfig = {}

async function getEnvConf() {
  const res = await axios.get(`${configServer}/remote-dashboard/${env}`)
  const property = res.data.propertySources[0].source
  for (let key in property) {
    if (key.includes('env.')) {
      envConfig[key.replace('env.', '')] = property[key]
    }
    if (key.includes('setting.')) {
      settingConfig[key.replace('setting.', '')] = property[key]
    }
  }
}
async function getUrlConf() {
  const res = await axios.get(`${configServer}/web-url/${env}`)
  const property = res.data.propertySources[0].source
  for (let key in property) {
    urlConfig[key.replace('url.', '')] = property[key]
  }
}

module.exports = {
  async init() {
    await Promise.all([getEnvConf(), getUrlConf()])
  },
  getAsNumber(key) {
    return Number(envConfig[key])
  },
  getAsString(key) {
    return String(envConfig[key])
  },
  getAsBoolean(key) {
    return Boolean(envConfig[key])
  },
  getPort() {
    return process.env.PORT || String(envConfig['PORT'])
  },
  getEnv() {
    return env
  },
  getUrls() {
    // const urls = {}
    // const env = this.getTargetEnv()
    // Object.keys(urlsConfig).forEach(key => {
    //   urls[key] = urlsConfig[key][env]
    // })
    // urlConfig['console'] = '/account'
    return urlConfig
  },
  getSettings() {
    // for test
    // settingConfig['ALLOW_SERVER_RECORD_FILE_INFO'] = false
    // settingConfig['ALLOW_LOCAL_RECORD_FILE_INFO'] = true
    // settingConfig['ALLOW_ATTACH_FILE_INFO'] = true
    return settingConfig
  },
  getConfigs() {
    return {
      ...this.getUrls(),
      settings: { ...this.getSettings() },
      runtime: this.getEnv(),
      timeout: this.getAsNumber('TIMEOUT'),
    }
  },
}
