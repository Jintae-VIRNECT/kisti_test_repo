const express = require('express')
const router = express.Router()
const config = require('./server/config')

router.get('/healthcheck', function (req, res) {
  res.send('200')
})

router.get('/configs', function (req, res) {
  res.header('Content-Type', 'application/json')
  res.send(JSON.stringify(config.getConfigs()))
})

router.get('/*', function (req, res) {
  res.redirect('/')
})

module.exports = router
