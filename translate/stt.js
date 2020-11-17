// Imports the Google Cloud client library
const speech = require('@google-cloud/speech')
const fs = require('fs')
const logger = require('../server/logger')

// Creates a client
let client = null
// const client = new speech.SpeechClient()

async function getStt(audioFile, languageCode, rateHertz) {
  if (client === null) {
    client = new speech.SpeechClient({
      keyFilename: './translate/remote-translation.json',
    })
  }
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

  try {
    // Detects speech in the audio file
    const [response] = await client.recognize(request)
    const transcription = response.results
      .map(result => result.alternatives[0].transcript)
      .join('\n')
    logger.log(`SUCCESS STT: ${transcription}`, 'STT_SYNC')
    return {
      code: 200,
      data: transcription,
      message: 'complete',
    }
  } catch (err) {
    logger.error(`${err.message}(${err.code})`, 'STT_SYNC')
    return {
      code: err.code,
      data: null,
      message: err.message,
    }
  }
}

module.exports = {
  getStt,
}
