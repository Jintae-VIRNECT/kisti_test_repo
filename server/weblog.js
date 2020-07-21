const logger = require('./logger')

module.exports = {
  putLogs: function(logs) {
    // const GATEWAY_SERVICE_URL = configService.getAsString('GATEWAY_SERVICE_URL');
    // const GATEWAY_ADMIN_URL = configService.getAsString('GATEWAY_ADMIN_URL');
    // const REMOTE_SDK_URL = configService.getAsString('REMOTE_SDK_URL');

    // const urls = JSON.stringify({
    //   GATEWAY_SERVICE_URL,
    //   GATEWAY_ADMIN_URL,
    //   REMOTE_SDK_URL,
    // })

    logger.log(logs, 'CONSOLE')

    return logs

    // fs.writeFile('url.json', writes, function (err) {
    //   if (err) {
    //     logger.error(`${err}`, 'READER')
    //     logger.error(`can not write file for url.`, 'READER')
    //     process.exit(1);
    //   } else {
    //     logger.log(writes, 'READER')
    //   }
    // })
  },
}
