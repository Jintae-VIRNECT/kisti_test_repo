const express = require('express')
const router = express.Router()
const path = require('path')

function IsAllowBrowser(req) {
  const userAgent = req.headers['user-agent']
  const isChrome = userAgent.includes('Chrome')
  const isChromeMobile =
    userAgent.includes('CriOS') || userAgent.includes('mobileApp')
  const IsHeartBeat = userAgent.includes('ELB-HealthChecker/2.0')
  const isEdge = userAgent.includes('Edge')

  return (isChrome || isChromeMobile || IsHeartBeat) && !isEdge
}

function IsMobileBrowser(req) {
  const userAgent = req.headers['user-agent']
  const isChromeMobile =
    userAgent.includes('Mobile') ||
    userAgent.includes('CriOS') ||
    userAgent.includes('mobileApp')

  return isChromeMobile
}

router.get('/workspace', function(req, res) {
  if (IsAllowBrowser(req)) {
    if (IsMobileBrowser(req)) {
      res.redirect('/m/qrcode')
    } else {
      res.sendFile(path.join(__dirname, '/dist/remote/index.html'))
    }
  } else {
    res.redirect('/support')
    return
  }
})

router.get('/service', function(req, res) {
  if (IsAllowBrowser(req)) {
    if (IsMobileBrowser(req)) {
      res.redirect('/m/qrcode')
    } else {
      res.sendFile(path.join(__dirname, '/dist/remote/index.html'))
    }
  } else {
    res.redirect('/support')
    return
  }
})

router.get('/support', function(req, res) {
  res.sendFile(path.join(__dirname, '/dist/extra/index.html'))
})

router.get('/*', function(req, res) {
  res.redirect('/remote')
})

module.exports = router
