import axios from 'axios'

/**
 * STT
 * @param {File} file 음성 녹음 파일
 * @param {String} lang stt language code
 * @param {String} rateHertz rate hertz
 */
const stt = async (file, lang, rateHertz = 48000) => {
  const { data } = await axios.post('/stt', {
    file,
    lang,
    rateHertz,
  })
  if (data.code !== 200) {
    throw { message: data.message, code: data.code }
  }
  return data.data
}

/**
 * TRANSLATE
 * @param {String} text 번역할 텍스트
 * @param {String} targetCode translate 타겟 언어코드
 */
const translate = async (text, targetCode) => {
  const { data } = await axios.post('/translate', {
    text: text,
    target: targetCode,
  })
  if (data.code !== 200) {
    throw { message: data.message, code: data.code }
  }
  return data.data
}

/**
 * TTS
 * @param {String} text tts 텍스트
 * @param {String} lang tts 언어코드
 * @param {String} voice 기본 음성 목소리 'male', 'female'
 */
const tts = async (text, lang, voice = 'FEMALE') => {
  const { data } = await axios.post('/tts', {
    text: text,
    lang: lang,
    voice: voice,
  })
  if (data.code !== 200) {
    throw { message: data.message, code: data.code }
  }
  return data.data
}

export { stt, translate, tts }
