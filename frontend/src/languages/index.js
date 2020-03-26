const files = require.context('.', true, /index.js$/)
const modules = {}
const exceptName = './index.js'

files.keys().forEach(key => {
	if (key === exceptName) return
	modules[key.replace(/(\.\/|\/index\.js)/g, '')] = files(key)
})

export default modules
