const express = require('express')
const route = require('./route')
const app = express()
const server = require('./server/module')
const path = require('path')
const helmet = require('helmet')

var bodyParser = require('body-parser')

app.use(
  helmet.frameguard({
    action: 'deny',
  }),
)

app.use(bodyParser.json())

app.use(route)

app.use(express.static(path.join(__dirname, 'dist')))

server
  .start(app)
  .then(function () {})
  .catch(function (e) {
    console.log(e)
  })
