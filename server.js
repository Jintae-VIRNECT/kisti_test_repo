const express = require('express')
const app = express()
const server = require('http').Server(app)
const https = require('https')
const fs = require('fs')
const path = require('path')
const route = require('./route')
const config = require('./configs/runtime')
const logger = require('@virnect/logger')

app.use(express.static(path.join(__dirname, './dist')))
app.use(route)


;(async () => {
	await config.init()
  const envSet = config.envConfig
  logger.info('server is running...', 'LISTENING')
  logger.ipInfo(`${envSet.SERVER_PORT}`, 'LISTENING')
  logger.info(`VIRNECT_ENV: ${process.env.VIRNECT_ENV}`, 'LISTENING')
  logger.info(`SSL_ENV: ${envSet.SSL_ENV}`, 'LISTENING')
  Object.entries(config.urlConfig).forEach(([key, val]) =>
    logger.info(`${key}: ${val}`, 'LISTENING'),
  )

	if (envSet.SSL_ENV === 'private') {
		const options = {
			key: fs.readFileSync(`./cert/${config.sslConfig.name}.key`),
			cert: fs.readFileSync(`./cert/${config.sslConfig.name}.crt`)
      // passphrase: config.sslConfig.pass
		}
		https.createServer(options, app).listen(envSet.SERVER_PORT)
	} else {
		server.listen(envSet.SERVER_PORT)
	}
})()
 