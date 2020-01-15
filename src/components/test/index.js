const files = require.context('.', false, /\.vue$/)
const modules = {}
// const exceptName = './Test.vue'
const exceptName = [
  './Test.vue',
  './before',
]

files.keys().forEach((key) => {
  for (var i = 0; i < exceptName.length; i++) {
    if(key === exceptName[i]) return
  }
  modules[key.replace(/(\.\/|\.vue)/g, '')] = files(key).default
})

module.exports = modules
