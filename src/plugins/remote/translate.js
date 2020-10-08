import axios from 'axios'

/**
 * STT
 * @param {File} file 음성 녹음 파일
 * @param {String} lang stt language code
 * @param {String} rateHertz rate hertz
 */
const stt = async (file, lang, rateHertz = 48000) => {
  try {
    const res = await axios.post('/stt', {
      file,
      lang,
      rateHertz,
    })
    return res.data
  } catch (err) {
    console.log(err)
  }
}

/**
 * TRANSLATE
 * @param {String} message 번역할 텍스트
 * @param {String} code translate 타겟 언어코드
 */
const translate = async (message, code) => {
  try {
    const res = await axios.post('/translate', {
      text: message,
      target: code,
    })
    return res.data
  } catch (err) {
    console.log(err)
  }
}

/**
 * TTS
 * @param {String} text tts 텍스트
 * @param {String} lang tts 언어코드
 * @param {String} voice 기본 음성 목소리 'male', 'female'
 */
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
