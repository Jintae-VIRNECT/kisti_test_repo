const express = require('express')
const router = express.Router()
const path = require('path')

router.get('/healthcheck', function(req, res) {
  res.send('200')
})

router.get('/*', function(req, res) {
  res.redirect('/')
})

module.exports = router
