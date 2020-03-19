import elementLocale from 'element-ui/lib/locale/lang/en'

const files = require.context('.', false, /\.json$/)
const modules = {}

files.keys().forEach(key => {
  if (key === './index.js') return
  Object.assign(modules, files(key))
})

module.exports = {
  language_abbr: 'ENG',
  ...elementLocale,
  ...modules,
}
