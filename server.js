const express = require('express')
const route = require('./route')
const app = express()
const server = require('./server/module')
const path = require('path')

var bodyParser = require('body-parser')
app.use(bodyParser.json())

app.use(express.static(path.join(__dirname, 'dist')))

app.use(route)

server
  .start(app)
  .then(function() {})
  .catch(function(e) {
    console.log(e)
  })
