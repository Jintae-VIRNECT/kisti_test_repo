const express = require('express')
const route = require('./route')
const app = express()
const server = require('./server/module')
const path = require('path')
const isbot = require('isbot')
const { metaHTML } = require('./server/metadata')

var bodyParser = require('body-parser')

app.use((req, res, next) => {
  const isBot = isbot(req.headers['user-agent'])
  const lang = req.acceptsLanguages('ko', 'en')

  if (isBot) {
    if (lang) {
      res.send(metaHTML(lang))
    } else {
      res.send(metaHTML('en'))
    }
    return
  }
  next()
})

app.use(bodyParser.json({ limit: '50mb' }))

app.use((req, res, next) => {
  res.header('X-Frame-Options', 'deny')
  next()
})

app.use(express.static(path.join(__dirname, 'dist')))
app.use(express.static(path.join(__dirname, 'record')))

const translate = require('./translate/translate')
const stt = require('./translate/stt')
const tts = require('./translate/tts')

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

app.use(route)

server
  .start(app)
  .then(function() {})
  .catch(function(e) {
    console.log(e)
  })
