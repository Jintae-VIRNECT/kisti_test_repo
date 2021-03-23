const express = require('express')
const route = require('./route')
const app = express()
const server = require('./server/module')
const path = require('path')
const helmet = require('helmet')
const config = require('./server/config')
const util = require('./server/util')
const meta = require('./server/meta')

const isbot = require('isbot')

var bodyParser = require('body-parser')

app.use(bodyParser.json())

app.use((req, res, next) => {
  const remoteAddr = config.getConfigs().remote
  const isBot = isbot(req.headers['user-agent'])
  const lang = req.acceptsLanguages('ko', 'en')

  if (isBot) {
    if (lang) {
      res.send(util.GenMetaHTML(meta[lang]))
    } else {
      res.send(util.GenMetaHTML(meta['en']))
    }
    return
  }

  if (util.IsAllowBrowser(req)) {
    if (util.IsMobileBrowser(req)) {
      res.redirect(remoteAddr + '/support')
      return
    }
    next()
  } else {
    res.redirect(remoteAddr + '/support')
    return
  }
})

const initHelmet = async () => {
  await config.init()

  const urls = config.getUrls()
  const allows = []
  const cntAllows = []

  for (const property in urls) {
    if (property.includes('minio')) {
      allows.push(urls[property])
    }

    if (property.includes('csp.wss')) {
      cntAllows.push(urls[property])
    } else if (property.includes('csp.')) {
      allows.push(urls[property])
    }
  }

  cntAllows.push(...allows)

  app.use(
    helmet({
      frameguard: {
        action: 'deny',
      },
      contentSecurityPolicy: {
        directives: {
          ...helmet.contentSecurityPolicy.getDefaultDirectives(),
          'script-src': ["'self'", ...allows],
          'style-src': [
            "'self'",
            'https:',
            'blob:',
            "'unsafe-inline'",
            ...allows,
          ],
          'connect-src': ["'self'", 'data:', ...cntAllows],
          'img-src': ["'self'", 'data:', ...allows],
          'media-src': ["'self'", ...allows],
        },
      },
    }),
  )
}

initHelmet().then(() => {
  app.use(express.static(path.join(__dirname, 'dist')))
  app.use(route)

  server
    .start(app)
    .then(function () {})
    .catch(function (e) {
      console.log(e)
    })
})
