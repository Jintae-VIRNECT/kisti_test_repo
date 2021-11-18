const fs = require('fs-extra')
const path = require('path')

function getConfigurationByFile(file) {
  const pathToConfigFile = path.resolve('./cypress/', 'config', `${file}.json`)
  return fs.readJson(pathToConfigFile)
}

// plugins file
module.exports = (on, config) => {
  // config 아래 json 파일을 읽어오되, 기본 값은 dev.json 파일을 읽어오도록 한다.
  if (config.testingType === 'component') {
    const { startDevServer } = require('@cypress/webpack-dev-server')
    const webpackConfig = require('../../build/webpack.base.conf')
    on('dev-server:start', options =>
      startDevServer({ options, webpackConfig }),
    )
  }
  const file = config.env.configFile || 'develop'
  return getConfigurationByFile(file)
}
