const fs = require('fs')

import elementKo from 'element-ui/lib/locale/lang/ko'
import elementEn from 'element-ui/lib/locale/lang/en'

/**
 * json 파일 로더
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
        val: require(`${dirname}/${file}`),
      }
    })
  const json = {}
  files.forEach(file => {
    if (/\.md$/.test(file.key)) {
      json[file.key] = file.val
    } else {
      Object.assign(json, file.val)
    }
  })

  return json
}

/**
 * i18n
 */
module.exports = {
  locales: ['ko', 'en', 'keyname'],
  defaultLocale: 'ko',
  strategy: 'no_prefix',
  detectBrowserLanguage: {
    useCookie: true,
    cookieKey: 'lang',
    cookieDomain: '.virnect.com',
  },
  vueI18n: {
    fallbackLocale: /(production|onpremise)/.test(process.env.VIRNECT_ENV)
      ? 'en'
      : null,
    messages: {
      ko: { ...elementKo, ...loader('/ko') },
      en: { ...elementEn, ...loader('/en') },
    },
  },
}
