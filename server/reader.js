const logger = require('./logger')
const configService = require('./config')

module.exports = {
  getURLs: function() {
    const urls = JSON.stringify(configService.getUrls())

    logger.log(urls, 'READER')

    return urls
  },
}
