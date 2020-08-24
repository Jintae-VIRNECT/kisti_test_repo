const files = require.context('.', false, /\.js$/)
const modules = {}
const exceptName = './index.js'

files.keys().forEach(name => {
  if (name === exceptName) return
  Object.keys(files(name)).forEach(key => {
    modules[key] = files(name)[key]
  })
})

module.exports = modules
