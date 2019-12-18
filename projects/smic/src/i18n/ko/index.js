import ko from 'element-ui/lib/locale/lang/ko'

const files = require.context('.', true, /.json$/)
const modules = {}
const exceptName = './index.js'

files.keys().forEach(key => {
	if (key === exceptName) return
	modules[key.replace(/(\.\/|\.json)/g, '')] = files(key)
})

export default {
	language_type: '한국어',
	...ko,
	...modules,
}
