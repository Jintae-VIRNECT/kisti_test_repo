const express = require('express')
const app = express()
const server = require('http').Server(app)
const https = require('https')
const fs = require('fs')
const path = require('path')
const route = require('./route')
const env = process.env.VIRNECT_ENV ? process.env.VIRNECT_ENV : process.env.NODE_ENV
const config = require('./configs/runtime')

app.use(express.static(path.join(__dirname, './dist')))
app.use(route)


;(async () => {
	await config.init()
	const envSet = config.envConfig
	console.log(env)
	if (/local|develop|onpremise/.test(env)) {
		console.log(config.urlConfig)
		const options = {
			key: fs.readFileSync('./cert/virnect.key'),
			cert: fs.readFileSync('./cert/virnect.crt')
		}
		https.createServer(options, app).listen(envSet.SERVER_PORT)
	} else {
		console.log(`${envSet.SERVER_PORT}`)
		server.listen(envSet.SERVER_PORT)
	}
})()
