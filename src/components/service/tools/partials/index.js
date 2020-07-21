const files = require.context('.', false, /\.vue$/)
const modules = {}

files.keys().forEach(key => {
  const rep = key.replace(/(\.\/Tool|\.\/Menu|\.vue$)/g, '')
  modules[rep] = files(key).default
})

module.exports = modules
