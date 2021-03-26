const express = require('express')
const route = require('./route')
const app = express()
const server = require('./server/module')
const path = require('path')
const compression = require('compression')
const helmet = require('helmet')
const config = require('./server/config')

var bodyParser = require('body-parser')

const translate = require('./translate/translate')
const stt = require('./translate/stt')
const tts = require('./translate/tts')

app.use(bodyParser.json({ limit: '50mb' }))
app.use(compression())

/* FEATURE: STT */
app.post('/translate', bodyParser.json(), function(req, res) {
  const text = req.body.text
  const target = req.body.target
  translate.getTranslate(text, target).then(value => {
    res.send(value)
  })
})

app.post('/stt', bodyParser.json(), function(req, res) {
  const file = req.body.file
  const lang = req.body.lang
  const rateHertz = req.body.rateHertz
  stt.getStt(file, lang, rateHertz).then(value => {
    console.log(req.body.lang, '::', value)
    res.send(value)
  })
})

app.post('/tts', bodyParser.json(), function(req, res) {
  // console.log(req.body.file)
  const text = req.body.text
  const lang = req.body.lang
  const voice = req.body.voice
  tts.getTts(text, lang, voice).then(value => {
    // console.log(req.body.text, '::', value)
    res.send(value)
  })
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
          'connect-src': ["'self'", 'blob:', 'data:', ...cntAllows],
          'img-src': ["'self'", 'data:', ...allows],
          'media-src': ["'self'", 'data:', ...allows],
          'worker-src': ["'self'", 'blob:', ...allows],
        },
      },
    }),
  )
}

initHelmet().then(() => {
  app.use(express.static(path.join(__dirname, 'dist')))
  app.use(express.static(path.join(__dirname, 'record')))

  app.use(route)

  server
    .start(app)
    .then(function() {})
    .catch(function(e) {
      console.log(e)
    })
})
