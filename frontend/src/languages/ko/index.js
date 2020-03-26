const files = require.context('.', false, /\.json$/)
const modules = {}

files.keys().forEach(key => {
	if (key === './index.js') return
	Object.assign(modules, files(key))
})

export default modules
