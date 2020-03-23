import fs from 'fs'

import elementKo from 'element-ui/lib/locale/lang/ko'

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
  locales: ['ko'],
  defaultLocale: 'ko',
  vueI18n: {
    fallbackLocale: 'ko',
    messages: {
      ko: { ...elementKo, ...loader('/ko') },
    },
  },
}
