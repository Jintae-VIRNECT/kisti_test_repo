const log4js = require('log4js')
const chalk = require('chalk')

log4js.configure({
  appenders: {
    default: {
      type: 'console',
      layout: {
        type: 'pattern',
        pattern:
          `${chalk.green('[RMWEB]')} - ` +
          `${chalk.white('[%d{yyyy-MM-dd hh:mm:ss}]')} - ` +
          `[%p] - ` +
          `${chalk.cyan(`[%c]`)} - ` +
          `%m%`,
      },
    },
    file: {
      type: 'dateFile',
      filename: './logs/rmweb',
      pattern: '.yyyy-MM-dd-hh.log',
      alwaysIncludePattern: true,
      layout: {
        type: 'pattern',
        pattern: `[%d{yyyy-MM-dd hh:mm:ss}] - [%p] - [%c] - %m`,
      },
    },
  },
  categories: {
    default: {
      appenders: ['default', 'file'],
      level: 'all',
    },
  },
})

module.exports = {
  log: function(message, context) {
    const logger = log4js.getLogger(context)
    logger.info(message)
  },
  error: function(message, context) {
    const logger = log4js.getLogger(context)
    logger.error(message)
  },
}
