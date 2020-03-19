// Imports the Google Cloud client library
const speech = require('@google-cloud/speech')
const fs = require('fs')

// Creates a client
const client = new speech.SpeechClient()

async function getStt(audioFile, languageCode, rateHertz) {
  const encoding = 'LINEAR16'
  const sampleRateHertz = 16000

  const config = {
    encoding: encoding,
    sampleRateHertz: rateHertz || sampleRateHertz,
    languageCode: languageCode,
    audioChannelCount: 2,
  }
  const audio = {
    content: audioFile,
  }

  const request = {
    config: config,
    audio: audio,
  }

  // Detects speech in the audio file
  const [response] = await client.recognize(request)
  const transcription = response.results
    .map(result => result.alternatives[0].transcript)
    .join('\n')
  return transcription
}

function getSocket(server) {
  const io = require('socket.io')(server, { origins: '*:*' })

  io.on('connection', socket => {
    console.log('New client connected: ' + socket.id)
    const clientData = {}
    clientData[socket.id] = {
      id: socket.id,
      speechClient: new speech.SpeechClient(),
      recognizeStream: null,
      restartTimeoutId: null,
      intervalTimerId: null,
      sttLanguageCode: 'en-US',
      currentResultEndTime: null,
      totalResultEndTime: 0,
      streamingLimit: 10000,
    }

    socket.on('setStreamingLimit', function(data) {
      clientData[socket.id].streamingLimit = data
    })

    socket.on('sttLanguageCode', function(data) {
      clientData[socket.id].sttLanguageCode = data
    })

    socket.on('startStreaming', function() {
      console.log('starting to stream')
      startStreaming()
    })

    socket.on('binaryStream', function(data) {
      if (clientData[socket.id].recognizeStream != null) {
        clientData[socket.id].recognizeStream.write(data)
      }
    })

    socket.on('stopStreaming', function() {
      clearTimeout(clientData[socket.id].restartTimeoutId)
      stopStreaming()
    })

    socket.on('disconnect', function() {
      console.log('client disconnected')
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
          console.error('STREAMING_RECOGNIZE::', error)
        })
        .on('data', emitCallback)

      clientData[socket.id].restartTimeoutId = setTimeout(
        restartStreaming,
        clientData[socket.id].streamingLimit,
      )
    }

    function stopStreaming() {
      clearInterval(clientData[socket.id].intervalTimerId)
      clientData[socket.id].recognizeStream = null
    }
    const emitCallback = stream => {
      socket.emit('getJSON', stream)
    }
    function restartStreaming() {
      console.log('RESTART_STREAMING>>>')
      clientData[socket.id].recognizeStream.removeListener('data', emitCallback)
      clientData[socket.id].recognizeStream = null
      stopStreaming()
      socket.emit('resetStreamOccurred', clientData[socket.id].streamingLimit)
      startStreaming()
    }
  })
}

module.exports = {
  getStt,
  getSocket,
}
