import en from 'element-ui/lib/locale/lang/en'

const files = require.context('.', true, /.json$/)
const modules = {}
const exceptName = './index.js'
files.keys().forEach(key => {
	if (key === exceptName) return
	modules[key.replace(/(\.\/|\.json)/g, '')] = files(key)
})

export default {
	language_type: 'English',
	...en,
	...modules,
}
