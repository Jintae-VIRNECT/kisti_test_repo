const express = require('express')
const app = express()
const server = require('http').Server(app)
const https = require('https')
const fs = require('fs')
const path = require('path')
const route = require('./route')
const env = process.env.VIRNECT_ENV
const config = require('./configs/runtime')

app.use(express.static(path.join(__dirname, './dist')))
app.use(route)


;(async () => {
	await config.init()
	const envSet = config.envConfig
	// console.log(envSet)

	if (env === 'local' || env === 'develop') {
		const options = {
			key: fs.readFileSync('./cert/key.pem'),
			cert: fs.readFileSync('./cert/cert.pem')
		}
		https.createServer(options, app).listen(envSet.SERVER_PORT)
	} else {
		console.log(`${envSet.SERVER_PORT}`)
		server.listen(envSet.SERVER_PORT)
	}
})()