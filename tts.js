// Imports the Google Cloud client library
const textToSpeech = require('@google-cloud/text-to-speech')
// Import other required libraries
const fs = require('fs')
const util = require('util')

async function getTts(text, lang) {
  // Creates a client
  const client = new textToSpeech.TextToSpeechClient()

  // The text to synthesize
  // const text = 'Hello, world!'

  // Construct the request
  const request = {
    input: { text: text },
    // Select the language and SSML Voice Gender (optional)
    voice: { languageCode: lang || 'en-US', ssmlGender: 'NEUTRAL' },
    // Select the type of audio encoding
    audioConfig: { audioEncoding: 'LINEAR16' },
  }
  console.log(request)

  // Performs the Text-to-Speech request
  const [response] = await client.synthesizeSpeech(request)
  // Write the binary audio content to a local file
  // const writeFile = util.promisify(fs.writeFile)
  // await writeFile('output.wav', response.audioContent, 'binary')
  // console.log('Audio content written to file: output.mp3')
  console.log(response.audioContent.toString('base64'))
  return response.audioContent.toString('base64')
}

function _arrayBufferToBase64(buffer) {
  var binary = ''
  var bytes = new Uint8Array(buffer)
  var len = bytes.byteLength
  for (var i = 0; i < len; i++) {
    binary += String.fromCharCode(bytes[i])
  }
  return window.btoa(binary)
}

module.exports = {
  getTts,
}
