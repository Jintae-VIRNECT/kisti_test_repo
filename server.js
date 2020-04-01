const express = require('express')
const route = require('./route')
const app = express()
const server = require('./server/module')
const path = require('path')
// const logger = require('./server/logger')
// const microphoneStream = require('./translate')
// const translate = require('./translate')
// const stt = require('./stt')
// const tts = require('./tts')

var bodyParser = require('body-parser')
app.use(bodyParser.json())

console.log(server)

// app.post('/translate', bodyParser.json(), function(req, res) {
//   const text = req.body.text
//   const target = req.body.target
//   translate.getTranslate(text, target).then(value => {
//     res.send(value)
//   })
// })

// app.post('/stt', bodyParser.json(), function(req, res) {
//   // console.log(req.body.file)
//   // const text = req.body.text
//   // const target = req.body.target
//   stt.getStt(req.body.file, req.body.lang, req.body.rateHertz).then(value => {
//     console.log(req.body.lang, '::', value)
//     res.send(value)
//   })
// })

// app.post('/tts', bodyParser.json(), function(req, res) {
//   tts.getTts(req.body.message, req.body.lang).then(value => {
//     // console.log(req.body.message, '::', value)
//     res.send(value)
//   })
// })

app.use(express.static(path.join(__dirname, 'dist')))

// microphoneStream()
// app.use('/record', (req, res, next) => {
// })

app.use(route)

server
  .start(app)
  .then(function() {})
  .catch(function(e) {
    console.log(e)
  })
