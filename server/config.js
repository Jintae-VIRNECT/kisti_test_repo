'use strict'
const env = process.env.VIRNECT_ENV.trim()
const configServer = process.env.CONFIG_SERVER.trim()

const axios = require('axios')

let envConfig = {}
let urlConfig = {}

module.exports = {
  async init() {
    //차후에 remote-web 제거하고 dashboard용 셋팅 필요함.
    const res = await axios.get(`${configServer}/remote-dashboard/${env}`)
    const property = res.data.propertySources[0].source
    for (let key in property) {
      if (key.includes('env.')) {
        envConfig[key.replace('env.', '')] = property[key]
      }
      if (key.includes('url.')) {
        urlConfig[key.replace('url.', '')] = property[key]
      }
    }
  },
  getAsNumber(key) {
    return Number(envConfig[key])
  },
  getAsString(key) {
    return String(envConfig[key])
  },
  getPort() {
    return process.env.PORT || String(envConfig['PORT'])
  },
  getUrls() {
    // const urls = {}
    // const env = this.getTargetEnv()
    // Object.keys(urlsConfig).forEach(key => {
    //   urls[key] = urlsConfig[key][env]
    // })
    return {
      runtime: env,
      ...urlConfig,
    }
  },
}
