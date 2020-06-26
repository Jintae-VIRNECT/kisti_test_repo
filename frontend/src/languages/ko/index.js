import locale from 'element-ui/lib/locale/lang/ko'

console.log('한궈')

const files = require.context('.', false, /\.json$/)
const modules = {}

files.keys().forEach(key => {
	if (key === './index.js') return
	Object.assign(modules, files(key))
})

module.exports = {
	languageAbbr: 'KOR',
	...locale,
	...modules,
}
