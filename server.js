const express = require('express')
const app = express()
// const server = require('http').Server(app)
const https = require('https');
const fs = require('fs');
const path = require('path')
const route = require('./route')

app.use(express.static(path.join(__dirname, './dist')))
app.use(route)

app.get('/', function(req, res) {
	res.send('Hello')
})

const port = process.env.PORT || 8883
// server.listen(port || 8883)

var options = {
  key: fs.readFileSync('./cert/key.pem'),
  cert: fs.readFileSync('./cert/cert.pem')
};
https.createServer(options, app).listen(port)