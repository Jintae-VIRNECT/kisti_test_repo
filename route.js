const express = require('express')
const router = express.Router()
const path = require('path')
const config = require('./server/config')
const url = require('url')

router.get('/healthcheck', function(req, res) {
  res.send('200')
})

router.get('/urls', function(req, res) {
  res.header('Content-Type', 'application/json')
  res.send(JSON.stringify(config.getUrls()))
})

router.get('/*', function(req, res) {
  res.redirect('/')
})

module.exports = router
