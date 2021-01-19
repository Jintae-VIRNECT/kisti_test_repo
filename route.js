const express = require('express')
const router = express.Router()
const path = require('path')
const config = require('./configs/runtime')

/**
 * @description
 * Main app route
 */

router.get('/urls', (req, res) => {
  res.header('Content-Type', 'application/json')
  res.header('X-Frame-Options', 'deny')
  res.send(JSON.stringify(config.urlConfig))
  // res.send(req.query.origin)
  // console.log(req.query.origin)
})

router.get('/healthcheck', (req, res) => {
  res.send('Hi Virnect')
})

router.get('/*', function (req, res) {
  res.header('X-Frame-Options', 'deny')
  res.sendFile(path.join(__dirname, '/dist/app.html'))
})

module.exports = router
