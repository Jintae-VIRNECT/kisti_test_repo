const express = require('express')
const app = express()
const server = require('http').Server(app)
const https = require('https')
const fs = require('fs')
const path = require('path')
const route = require('./route')
const dotenv = require('dotenv')
const filePath = `.env.${process.env.VIRNECT_ENV.trim()}`
const env = dotenv.parse(fs.readFileSync(filePath))

app.use(express.static(path.join(__dirname, './dist')))
app.use(route)

const port = env.SERVER_PORT
console.log(`${env.LOCAL_HOST}:${env.SERVER_PORT}`)

if (process.env.VIRNECT_ENV === 'local' || process.env.VIRNECT_ENV === 'develop') {
	const options = {
		key: fs.readFileSync('./cert/key.pem'),
		cert: fs.readFileSync('./cert/cert.pem')
	}
	https.createServer(options, app).listen(port)
} else {
	server.listen(port)
}
