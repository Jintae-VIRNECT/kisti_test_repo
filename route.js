const express = require('express')
const router = express.Router()
const path = require('path')
const config = require('./server/config')
const url = require('url')
const fs = require('fs')
const { metaHEAD } = require('./server/metadata')

const acceptLang = req => {
  const lang = req.acceptsLanguages('ko', 'en')
  if (lang) {
    return lang
  } else {
    return 'en'
  }
}

const extraHtml = fs.readFileSync('./dist/extra/index.html', 'utf8')
const remoteHtml = fs.readFileSync('./dist/remote/index.html', 'utf8')
const ieHtmlEn = fs.readFileSync('./static/ie/en.html', 'utf8')
const ieHtmlKr = fs.readFileSync('./static/ie/kr.html', 'utf8')

let extra = {
  en: extraHtml,
  ko: extraHtml,
}
let remote = {
  en: remoteHtml,
  ko: remoteHtml,
}

if (config.getEnv() !== 'onpremise') {
  extra = {
    en: metaHEAD(extraHtml, 'en'),
    ko: metaHEAD(extraHtml, 'ko'),
  }
  remote = {
    en: metaHEAD(remoteHtml, 'en'),
    ko: metaHEAD(remoteHtml, 'ko'),
  }
}

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

function IsIE(req) {
  const userAgent = req.headers['user-agent'] || ''
  return userAgent.includes('Trident')
}

function RouteSupportOrIE(req, res) {
  const isIE = IsIE(req)
  if (isIE) {
    const lang = acceptLang(req)
    if (lang === 'ko') {
      res.send(ieHtmlKr)
    } else {
      res.send(ieHtmlEn)
    }
  } else {
    res.redirect('/support')
  }
}

router.get('/healthcheck', function(req, res) {
  res.send('200')
})

//spot 페이지 추가
if (config.getEnv() === 'onpremise') {
  router.get('/spot-control', function(req, res) {
    const lang = acceptLang(req)
    res.send(remote[lang])
  })

  router.get('/spot-error', function(req, res) {
    const lang = acceptLang(req)
    res.send(remote[lang])
  })
}

router.get('/home', function(req, res) {
  if (IsAllowBrowser(req)) {
    if (IsMobileBrowser(req)) {
      RouteSupportOrIE(req, res)
    } else {
      const lang = acceptLang(req)
      res.send(remote[lang])
    }
  } else {
    RouteSupportOrIE(req, res)
    return
  }
})

router.get('/service', function(req, res) {
  if (IsAllowBrowser(req)) {
    if (IsMobileBrowser(req)) {
      RouteSupportOrIE(req, res)
    } else {
      const lang = acceptLang(req)
      res.send(remote[lang])
    }
  } else {
    RouteSupportOrIE(req, res)
    return
  }
})

router.get('/support', function(req, res) {
  // res.sendFile(path.join(__dirname, '/dist/extra/index.html'))
  const lang = acceptLang(req)
  res.send(extra[lang])
})

if (config.getEnv() !== 'onpremise') {
  router.get('/policy/*', function(req, res) {
    const lang = acceptLang(req)
    res.send(extra[lang])
  })

  router.get('/policy', function(req, res) {
    res.redirect('/policy/terms')
  })

  router.get('/OSS/*', function(req, res) {
    const lang = acceptLang(req)
    res.send(extra[lang])
  })

  router.get('/OSS', function(req, res) {
    const lang = acceptLang(req)
    res.send(extra[lang])
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

//pwa service worker
router.get('/sw.js', function(req, res) {
  res.sendFile(path.join(__dirname, '/static/js/sw.js'))
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
