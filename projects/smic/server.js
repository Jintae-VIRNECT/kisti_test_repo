const express = require('express')
const route = require('./route')
const app = express()
// const redirectToHTTPS = require('express-http-to-https').redirectToHTTPS
const server = require('./server/module')
// const path = require('path')
const logger = require('./server/logger')
var bodyParser = require('body-parser')
app.use(bodyParser.json())

// app.use(express.static(path.join(__dirname, 'dist')))
app.use('/dist', express.static(__dirname + '/dist'))

app.post('/logs', bodyParser.json(), function(req, res) {
  res.header('Content-Type', 'application/json')
  let type = req.body.type
  if (!type) type = 'CONSOLE'
  logger.log(req.body.data, type)
  res.send(true)
})

app.use(route)

server
  .start(app)
  .then(function() {})
  .catch(function(e) {
    console.log(e)
  })
