const fs = require('fs')

function loader() {
  const dir = fs.readdirSync(__dirname)
  const files = dir
    .filter(file => file !== 'index.js')
    .map(file => {
      return {
        key: file.replace('.json', ''),
        val: fs.readFileSync(`${__dirname}/${file}`, 'utf8'),
      }
    })
  const json = {}
  files.forEach(file => {
    json[file.key] = JSON.parse(file.val)
  })

  return json
}

module.exports = loader()
