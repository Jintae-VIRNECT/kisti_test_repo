const express = require('express')
const router = express.Router()
const path = require('path')
const config = require('./server/config')

function IsAllowBrowser(req) {
  const userAgent = req.headers['user-agent'] || ''
  const isChrome = userAgent.includes('Chrome')
  const isChromeMobile =
    userAgent.includes('CriOS') || userAgent.includes('mobileApp')
  const isEdge = userAgent.includes('Edg') || userAgent.includes('Edge')
  const isSamsung = userAgent.includes('SamsungBrowser')

  const isSafari = !isChrome && !isChromeMobile && userAgent.includes('Safari')

  return ((isChrome || isEdge || isChromeMobile) && !isSamsung) || isSafari
}

function IsMobileBrowser(req) {
  const userAgent = req.headers['user-agent'] || ''
  const isChromeMobile =
    userAgent.includes('Mobile') ||
    userAgent.includes('CriOS') ||
    userAgent.includes('mobileApp') ||
    userAgent.includes('iPhone')

  return isChromeMobile
}

router.get('/healthcheck', function (req, res) {
  res.send('200')
})

router.get('/configs', function (req, res) {
  // req.query.origin
  res.header('Content-Type', 'application/json')
  res.send(JSON.stringify(config.getConfigs()))
})

router.get('/', function (req, res) {
  const remoteAddr = config.getConfigs().remote

  if (IsAllowBrowser(req)) {
    if (IsMobileBrowser(req)) {
      res.redirect(remoteAddr + '/support')
    } else {
      res.sendFile(path.join(__dirname, '/dist/index.html'))
    }
  } else {
    res.redirect(remoteAddr + '/support')
    return
  }
})

router.get('/*', function (req, res) {
  res.redirect('/')
})

module.exports = router
