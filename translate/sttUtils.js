// Imports the Google Cloud client library
const speech = require('@google-cloud/speech')
// const fs = require('fs')
const logger = require('../server/logger')
const config = require('../server/config')
const cors = require('cors')

const getSocket = server => {
  const VIRNECT_ENV = process.env.VIRNECT_ENV || 'production'
  const io = require('socket.io')(server, {
    path: '/stt',
    pingInterval: 1000,
    pingTimeout: config.getAsNumber('TIMEOUT'),
  })
  if (VIRNECT_ENV === 'develop') {
    io.cors = cors()
  }

  io.on('connection', socket => {
    const sttCode = socket.handshake.query.lang
    logger.log(`SOCKET: ${socket.id} : CONNECT [${sttCode}]`, 'STT_STREAMING')
    const clientData = {}
    clientData[socket.id] = {
      id: socket.id,
      speechClient: new speech.SpeechClient({
        keyFilename: './translate/remote-translation.json',
      }),
      recognizeStream: null,
      restartTimeoutId: null,
      intervalTimerId: null,
      sttLanguageCode: sttCode,
      currentResultEndTime: null,
      totalResultEndTime: 0,
      streamingLimit: 60000,
    }

    socket.on('setStreamingLimit', function(data) {
      clientData[socket.id].streamingLimit = data
    })

    socket.on('sttLanguageCode', function(data) {
      clientData[socket.id].sttLanguageCode = data
    })

    socket.on('startStreaming', function() {
      logger.log(`SOCKET: ${socket.id} : START STREAMING`, 'STT_STREAMING')
      startStreaming()
    })

    socket.on('binaryStream', function(data) {
      if (clientData[socket.id].recognizeStream != null) {
        clientData[socket.id].recognizeStream.write(data)
      }
    })

    socket.on('stopStreaming', function() {
      logger.log(`SOCKET: ${socket.id} : STOP STREAMING`, 'STT_STREAMING')
      clearTimeout(clientData[socket.id].restartTimeoutId)
      stopStreaming()
    })

    socket.on('disconnect', function() {
      logger.log(`SOCKET: ${socket.id} : DISCONNECT`, 'STT_STREAMING')
    })

    function intervalTimer(restartTime, interval) {
      clientData[socket.id].intervalTimerId = setInterval(() => {
        socket.emit('timer', new Date() - restartTime)
        const timepassed = new Date() - restartTime
        if (timepassed % 10 == 0) {
          // console.log(timepassed);
        }
      }, interval)
    }
    function startStreaming() {
      intervalTimer(new Date(), 10)

      const sttRequest = {
        config: {
          encoding: 'LINEAR16',
          sampleRateHertz: 16000,
          languageCode: clientData[socket.id].sttLanguageCode,
          enableAutomaticPunctuation: false,
          enableWordTimeOffsets: true,
        },
        interimResults: true,
      }

      clientData[socket.id].recognizeStream = clientData[socket.id].speechClient
        .streamingRecognize(sttRequest)
        .on('error', error => {
          if (error.code === 11) {
            logger.error(
              `SOCKET: ${socket.id} : (${error.code}) ${error.message}`,
              'STT_STREAMING',
            )
            socket.emit('audioTimeout', error)
            // restartStream();
            // console.error('API request error ' + error)
          } else {
            logger.error(
              `SOCKET: ${socket.id} : (${error.code}) ${error.message}`,
              'STT_STREAMING',
            )
          }
        })
        .on('data', speechCallback)

      clientData[socket.id].restartTimeoutId = setTimeout(
        restartStreaming,
        clientData[socket.id].streamingLimit,
      )
    }

    function stopStreaming() {
      clearInterval(clientData[socket.id].intervalTimerId)
      if (clientData[socket.id].recognizeStream) {
        clientData[socket.id].recognizeStream.end()
      }
      clientData[socket.id].recognizeStream = null
    }
    const speechCallback = stream => {
      socket.emit('speechCallback', stream)
    }
    function restartStreaming() {
      logger.log(`SOCKET: ${socket.id} : RESTART STREAMING`, 'STT_STREAMING')
      clientData[socket.id].recognizeStream.end()
      clientData[socket.id].recognizeStream.removeListener(
        'data',
        speechCallback,
      )
      clientData[socket.id].recognizeStream = null
      stopStreaming()
      socket.emit('resetStreamOccurred', clientData[socket.id].streamingLimit)
      startStreaming()
    }
  })
}

module.exports = {
  getSocket,
}
