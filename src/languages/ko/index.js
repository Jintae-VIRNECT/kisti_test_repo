import locale from 'element-ui/lib/locale/lang/ko'

const files = require.context('.', false, /\.json$/)
const markdowns = require.context('.', false, /\.md$/)
const modules = {}

files.keys().forEach(key => {
  if (key === './index.js') return
  Object.assign(modules, files(key))
})
markdowns.keys().forEach(key => {
  modules[key.replace('./', '')] = markdowns(key).default
})

module.exports = {
  languageAbbr: 'KOR',
  ...locale,
  ...modules,
}
