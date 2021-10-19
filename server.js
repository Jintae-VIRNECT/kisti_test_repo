const express = require('express')
const route = require('./route')
const app = express()
const server = require('./server/module')
const { initHelmet } = require('./server/helmet')
const path = require('path')
const compression = require('compression')

const translate = require('./translate/translate')
const stt = require('./translate/stt')
const tts = require('./translate/tts')

app.use(express.json({ limit: '50mb' }))
app.use(compression())

/* FEATURE: STT */
app.post('/translate', express.json(), function(req, res) {
  const text = req.body.text
  const target = req.body.target
  translate.getTranslate(text, target).then(value => {
    res.send(value)
  })
})

app.post('/stt', express.json(), function(req, res) {
  const file = req.body.file
  const lang = req.body.lang
  const rateHertz = req.body.rateHertz
  stt.getStt(file, lang, rateHertz).then(value => {
    console.log(req.body.lang, '::', value)
    res.send(value)
  })
})

app.post('/tts', express.json(), function(req, res) {
  // console.log(req.body.file)
  const text = req.body.text
  const lang = req.body.lang
  const voice = req.body.voice
  tts.getTts(text, lang, voice).then(value => {
    // console.log(req.body.text, '::', value)
    res.send(value)
  })
})

initHelmet(app).then(() => {
  app.use(express.static(path.join(__dirname, 'dist')))
  app.use(express.static(path.join(__dirname, 'record')))
  app.use('/static', express.static(path.join(__dirname, 'static')))

  console.log(path.join(__dirname, 'static'))
  app.use(route)

  server
    .start(app)
    .then(function() {})
    .catch(function(e) {
      console.log(e)
    })
})
