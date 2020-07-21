const files = require.context('.', false, /\.js$/)
const modules = {}
const exceptName = './index.js'

files.keys().forEach(key => {
  if (key === exceptName) return
  modules[key.replace(/(\.\/|\.js)/g, '')] = files(key).default
})

export default modules
