const dotenv = require('dotenv')
const fs = require('fs')
const filePath = `.env.${process.env.NODE_ENV.trim()}`
const env = dotenv.parse(fs.readFileSync(filePath))

import elementKo from 'element-ui/lib/locale/lang/ko'
import elementEn from 'element-ui/lib/locale/lang/en'

/**
 * json 파일 로더
 * 파일명이 최상위 네임스페이스가 됨
 * messages.json => messages.deleteSuccess
 * @param {String} foldername
 */
function loader(foldername) {
  const dirname = `${__dirname}/${foldername}`
  const dir = fs.readdirSync(dirname)
  const files = dir
    .filter(file => file !== 'index.js')
    .map(file => {
      return {
        key: file.replace('.json', ''),
        val: fs.readFileSync(`${dirname}/${file}`, 'utf8'),
      }
    })
  const json = {}
  files.forEach(file => {
    json[file.key] = JSON.parse(file.val)
  })

  return json
}

/**
 * i18n
 */
module.exports = {
  locales: ['ko', 'keyname'],
  defaultLocale: 'ko',
  strategy: 'no_prefix',
  detectBrowserLanguage: {
    useCookie: true,
    cookieKey: 'lang',
    cookieDomain: '.virnect.com',
  },
  vueI18n: {
    fallbackLocale: env.TARGET_ENV === 'production' ? 'ko' : null,
    messages: {
      ko: { ...elementKo, ...loader('/ko') },
      // en: { ...elementEn, ...loader('/en') },
    },
  },
}
