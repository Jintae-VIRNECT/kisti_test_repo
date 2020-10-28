const express = require('express')
const router = express.Router()
const path = require('path')
const config = require('./server/config')
// const url = require('url')

function IsAllowBrowser(req) {
  // const userAgent = req.headers['user-agent'] || ''
  // const isChrome = userAgent.includes('Chrome')
  // const isChromeMobile =
  //   userAgent.includes('CriOS') || userAgent.includes('mobileApp')
  // const isEdge = userAgent.includes('Edg') || userAgent.includes('Edge')
  // const isSamsung = userAgent.includes('SamsungBrowser')

  // const findSafari = userAgent.includes('Safari')
  // const isSafari = !isChrome && !isChromeMobile && findSafari ? true : false

  // return (isChrome || isEdge || isChromeMobile) && !isSafari && !isSamsung
  return true
}

function IsMobileBrowser(req) {
  // const userAgent = req.headers['user-agent'] || ''
  // const isChromeMobile =
  //   userAgent.includes('Mobile') ||
  //   userAgent.includes('CriOS') ||
  //   userAgent.includes('mobileApp')

  // return isChromeMobile
  return true
}

router.get('/healthcheck', function(req, res) {
  res.send('200')
})

router.get('/urls', function(req, res) {
  res.header('Content-Type', 'application/json')
  res.send(JSON.stringify(config.getUrls()))
})

router.get('/configs', function(req, res) {
  // req.query.origin
  res.header('Content-Type', 'application/json')
  res.send(JSON.stringify(config.getConfigs()))
})

router.get('/home', function(req, res) {
  if (IsAllowBrowser(req)) {
    if (IsMobileBrowser(req)) {
      res.redirect('/support')
    } else {
      res.sendFile(path.join(__dirname, '/dist/index.html'))
    }
  } else {
    res.redirect('/support')
    return
  }
})

router.get('/*', function(req, res) {
  res.redirect('/home')
})

module.exports = router
