const https = require('https')
const http = require('http')
const path = require('path')
const os = require('os')
const fs = require('fs')
const logger = require('./logger')

var ServerModule = (function() {
  'use strict'

  let instance

  const NODE_ENV = process.env.NODE_ENV || 'production'
  const SSL_ENV = NODE_ENV === 'production' ? 'public' : 'private'
  const PORT = process.env.PORT || 8886

  function start(app) {
    return new Promise(function(resolve, reject) {
      process.on('uncaughtException', onProcessError)

      const options = {
        key: fs.readFileSync(path.join('./ssl/server.key')),
        cert: fs.readFileSync(path.join('./ssl/server.crt')),
      }

      try {
        if (SSL_ENV === 'public') {
          instance = http.createServer(app)
        } else {
          instance = https.createServer(options, app)
        }

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
    logger.log(`server is running...`, 'LISTENING')
    logger.log(`ip: ${getServerIp()}:${PORT}`, 'LISTENING')
    logger.log(`NODE ENV: ${NODE_ENV}`, 'LISTENING')
    logger.log(`SSL ENV: ${SSL_ENV}`, 'LISTENING')
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
