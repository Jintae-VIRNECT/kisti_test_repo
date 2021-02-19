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

app.use(
  helmet.frameguard({
    action: 'deny',
  }),
)

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
  }

  if (util.IsAllowBrowser(req)) {
    if (util.IsMobileBrowser(req)) {
      res.redirect(remoteAddr + '/support')
    }
    next()
  } else {
    res.redirect(remoteAddr + '/support')
  }
})

app.use(express.static(path.join(__dirname, 'dist')))

app.use(route)

server
  .start(app)
  .then(function () {})
  .catch(function (e) {
    console.log(e)
  })
