import openSocket from 'socket.io-client'
export let socket = null
let lastEndTime = 0

export const connect = () => {
  socket = openSocket('wss://192.168.13.64:8885')
  getTimeout()
}

export const disconnect = cb => {
  socket.disconnect()
  socket = null
}
export const getJSON = cb => {
  socket.on('speechCallback', response => cb(null, response))
}
export const subscribeToTimer = cb => {
  socket.on('timer', timestamp => cb(null, timestamp))
}
export const setStreamingLimit = restartTime => {
  socket.emit('setStreamingLimit', restartTime)
}
export const setSTTLanguageCode = sttCode => {
  socket.emit('sttLanguageCode', sttCode)
}
export const getTimeout = () => {
  socket.on('audioTimeout', err => {
    console.log(err)
  })
}
export const getTranscript = cb => {
  socket.on('speechCallback', response => {
    let transcript = response.results[0].alternatives[0].transcript
    let isFinal = response.results[0].isFinal
    let endTime =
      response.results[0].resultEndTime.seconds * 1000 +
      Math.round(response.results[0].resultEndTime.nanos / 1000000)
    let startTime = lastEndTime
    if (isFinal) {
      startTime =
        response.results[0].alternatives[0].words[0].startTime.seconds * 1000 +
        Math.round(
          response.results[0].alternatives[0].words[0].startTime.nanos /
            1000000,
        )
      lastEndTime = endTime
    }
    const transcriptObject = {
      transcript: transcript,
      isFinal: isFinal,
      startTime: startTime,
      endTime: endTime,
      isRestart: false,
    }
    cb(null, transcriptObject)
  })
}
export const requestRestarted = cb => {
  socket.on('resetStreamOccurred', data => {
    const restartObject = {
      transcript: 'Restart',
      isFinal: true,
      startTime: data,
      endTime: data,
      isRestart: true,
    }
    cb(null, restartObject)
  })
}
