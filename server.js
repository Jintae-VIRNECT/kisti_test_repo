const express = require('express')
const app = express()
const server = require('https').Server(app)
const path = require('path')
const route = require('./route')

app.use(express.static(path.join(__dirname, './dist')))
app.use(route)

app.get('/', function(req, res) {
	res.send('Hello')
})

const port = process.env.PORT || 8883
server.listen(port || 8883)
