const express = require('express')
const router = express.Router()
const path = require('path')
const config = require('./server/config')
const url = require('url')

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
  return false
  const userAgent = req.headers['user-agent'] || ''
  const isChromeMobile =
    userAgent.includes('Mobile') ||
    userAgent.includes('CriOS') ||
    userAgent.includes('mobileApp') ||
    userAgent.includes('iPhone')

  return isChromeMobile
}

router.get('/healthcheck', function(req, res) {
  res.send('200')
})

router.get('/home', function(req, res) {
  if (IsAllowBrowser(req)) {
    if (IsMobileBrowser(req)) {
      res.redirect('/support')
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
      res.redirect('/support')
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

if (config.getEnv() !== 'onpremise') {
  router.get('/policy/*', function(req, res) {
    res.sendFile(path.join(__dirname, '/dist/extra/index.html'))
  })

  router.get('/policy', function(req, res) {
    res.redirect('/policy/terms')
  })

  router.get('/OSS/*', function(req, res) {
    res.sendFile(path.join(__dirname, '/dist/extra/index.html'))
  })

  router.get('/OSS', function(req, res) {
    res.sendFile(path.join(__dirname, '/dist/extra/index.html'))
  })
}

router.get('/configs', function(req, res) {
  // req.query.origin
  res.header('Content-Type', 'application/json')
  res.send(JSON.stringify(config.getConfigs()))
})

router.get('/pdf.worker', function(req, res) {
  res.sendFile(path.join(__dirname, '/static/js/pdf.worker.js'))
})

router.get('/record', function(req, res) {
  const parsedURL = url.parse(req.url, true)
  const query = parsedURL.query

  const token = query.token
  const options = query.options
  const recorder = query.recorder

  if (token !== undefined && options !== undefined && recorder !== undefined) {
    res.sendFile(path.join(__dirname, '/record/record.html'))
  } else {
    res.redirect('/home')
  }
})

router.get('/*', function(req, res) {
  res.redirect('/home')
})

module.exports = router
