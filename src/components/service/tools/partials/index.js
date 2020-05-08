const files = require.context('.', false, /^\.\/Tool.*\.vue$/)
const modules = {}

files.keys().forEach(key => {
  const rep = key.replace(/(\.\/Tool|\.vue$)/g, '')
  modules[rep] = files(key).default
})

module.exports = modules
