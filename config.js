const fs = require('fs')
const axios = require('axios')
const dotenv = require('dotenv')
const env = dotenv.parse(fs.readFileSync('.env.local'))
const pjson = require('./package.json')

/**
 * get env from config server
 * @param {string} serviceName
 * @param {string} branch
 */
async function getConfig(serviceName, branch) {
  const { data } = await axios.get(
    `${
      process.env.CONFIG_SERVER || 'http://192.168.6.3:6383'
    }/${serviceName}/${branch}`,
  )
  return data.propertySources[0].source
}

module.exports = async () => {
  env.NODE_ENV = process.env.NODE_ENV
  env.VIRNECT_ENV = process.env.VIRNECT_ENV
  env.PROJECT_NAME = pjson.name || ''
  env.PROJECT_VERSION = pjson.version || '0.0.0'

  // local
  if (process.env.VIRNECT_ENV === 'local') {
    env.URLS = await getConfig('web-url', 'develop')
    for (const key in env.URLS) {
      env.URLS[key] = /api|ws/.test(key)
        ? env.URLS[key]
        : env.URLS[key].replace('https://192.168.6.3', 'https://localhost')
    }
  }
  // not local
  else {
    env.URLS = await getConfig('web-url', env.VIRNECT_ENV)
    const serverConfig = await getConfig('download-web', env.VIRNECT_ENV)
    for (const key in serverConfig) {
      env[key] = serverConfig[key]
    }
  }

  return env
}
