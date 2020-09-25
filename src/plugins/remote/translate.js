import axios from 'axios'

const stt = async (file, lang, rateHertz) => {
  try {
    const res = await axios.post('/stt', {
      file: file,
      lang: lang,
      rateHertz: rateHertz || 48000,
    })
    return res.data
  } catch (err) {
    console.log(err)
  }
}

const translate = async (message, code, voice) => {
  try {
    const res = await axios.post('/translate', {
      text: message,
      target: code,
      voice: voice,
    })
    return res.data
  } catch (err) {
    console.log(err)
  }
}

const tts = async (text, lang, voice) => {
  try {
    const res = await axios.post('/tts', {
      text: text,
      lang: lang,
      voice: voice,
    })
    return res.data
  } catch (err) {
    console.log(err)
  }
}

module.exports = {
  stt,
  translate,
  tts,
}
