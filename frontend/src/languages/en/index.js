import locale from 'element-ui/lib/locale/lang/en'

console.log('영어')
const files = require.context('.', false, /\.json$/)
const modules = {}

files.keys().forEach(key => {
	if (key === './index.js') return
	Object.assign(modules, files(key))
})

module.exports = {
	languageAbbr: 'ENG',
	...locale,
	...modules,
}
