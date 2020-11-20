const https = require('https')
const http = require('http')
const path = require('path')
const os = require('os')
const fs = require('fs')
const logger = require('./logger')
const config = require('./config')
const { getSocket } = require('../translate/sttUtils')

var ServerModule = (function() {
  'use strict'

  let instance

  let VIRNECT_ENV
  let SSL_ENV
  let PORT

  async function start(app) {
    await config.init()

    VIRNECT_ENV = process.env.VIRNECT_ENV || 'production'
    SSL_ENV = config.getAsString('SSL_ENV') || 'public'
    PORT = config.getPort() || 8886

    return new Promise(function(resolve, reject) {
      process.on('uncaughtException', onProcessError)

      const options = {
        key: fs.readFileSync(path.join('./ssl/virnect.key')),
        cert: fs.readFileSync(path.join('./ssl/virnect.crt')),
      }

      try {
        if (SSL_ENV === 'public') {
          instance = http.createServer(app)
        } else {
          instance = https.createServer(options, app)
        }
        getSocket(instance)

        instance
          .on('listening', onListening)
          .on('close', onClose)
          .on('error', onError)

        instance.listen(PORT)

        resolve()
      } catch (e) {
        reject(e)
      }
    })
  }

  function onError(err) {
    switch (err.code) {
      case 'EACCES':
        logger.error(`${err.code}`, 'ERROR')
        logger.error(`requires elevated privileges.`, 'ERROR')
        process.exit(1)
        break
      case 'EADDRINUSE':
        logger.error(`${err.code}`, 'ERROR')
        logger.error(`port ${PORT} already in use.`, 'ERROR')
        process.exit(1)
        break
      default:
        throw err
    }
  }

  function onClose(err) {
    logger.error(`server is closing...`, 'CLOSE')
    logger.error(`${err}`, 'CLOSE')
  }

  function onListening() {
    logger.info(`server is running...`, 'LISTENING')
    logger.info(`ip: ${getServerIp()}:${PORT}`, 'LISTENING')
    logger.info(`VIRNECT_ENV: ${VIRNECT_ENV}`, 'LISTENING')
    logger.info(`SSL_ENV: ${SSL_ENV}`, 'LISTENING')

    const urls = config.getUrls()
    delete urls.runtime
    Object.keys(urls).forEach(key => {
      logger.info(`${key.toUpperCase()}: ${urls[key]}`, 'LISTENING')
    })
  }

  function onProcessError(err) {
    logger.error(`${err.message}`, 'PROCESS_ERROR')
    logger.error(`${err.stack}`, 'PROCESS_ERROR')
    process.exit(1)
  }

  function getServerIp() {
    const ifaces = os.networkInterfaces()
    let result = ''
    for (const dev in ifaces) {
      let alias = 0
      // tslint:disable-next-line: ter-arrow-parens
      ifaces[dev].forEach(details => {
        if (details.family === 'IPv4' && !details.internal) {
          result = details.address
          // tslint:disable-next-line: no-increment-decrement
          ++alias
        }
      })
    }

    return result
  }

  return {
    start: start,
  }
})()

module.exports = ServerModule
