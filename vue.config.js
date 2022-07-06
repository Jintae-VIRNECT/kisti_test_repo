const path = require('path')
const fs = require('fs')
const host = '0.0.0.0'
const port = '9989'
let logger = null
let configService = null

//only work in local env
if (process.env.NODE_ENV === 'develop') {
  logger = require('./server/logger')
  configService = require('./server/config')
}

module.exports = {
  pages: {
    index: {
      entry: 'src/pages/dashboard/dashboard.js',
      template: 'public/index.html',
      filename: 'index.html',
      title: 'Dashboard',
      chunks: ['chunk-vendors', 'chunk-common', 'index'],
    },
    account: {
      entry: 'src/pages/account/account.js',
      template: 'public/account.html',
      filename: 'account.html',
      title: 'Login',
      chunks: ['chunk-vendors', 'chunk-common', 'account'],
    },
  },
  configureWebpack: {
    resolve: {
      modules: ['node_modules', 'modules'],
      alias: {
        '@': path.join(__dirname, '../'),
        components: path.join(__dirname, '/src/components'),
        assets: path.join(__dirname, '/src/assets'),
        plugins: path.join(__dirname, '/src/plugins'),
        api: path.join(__dirname, '/src/api'),
        utils: path.join(__dirname, '/src/utils'),
        mixins: path.join(__dirname, '/src/mixins'),
        configs: path.join(__dirname, '/src/configs'),
        stores: path.join(__dirname, '/src/stores'),
        routers: path.join(__dirname, '/src/routers'),
        languages: path.join(__dirname, '/src/languages'),
      },
    },
  },

  devServer: {
    https: {
      key: fs.readFileSync(path.join(__dirname, './ssl/virnect.key')),
      cert: fs.readFileSync(path.join(__dirname, './ssl/virnect.crt')),
    },
    host,
    port,
    proxy: {
      '/api': {
        target: 'https://192.168.0.9:8073',
        headers: {
          'Access-Control-Allow-Origin': '*',
        },
        secure: false,
        changeOrigin: true,
      },
    },
    noInfo: true,
    open: false,
    before: function (app) {
      var bodyParser = require('body-parser')
      configService.init()
      app.use(
        bodyParser.json({
          limit: '50mb',
        }),
      )

      app.post('/logs', bodyParser.json(), function (req, res) {
        logger.log(req.body.data, 'CONSOLE')
        res.send(true)
      })

      app.get('/configs', bodyParser.json(), function (req, res) {
        const a = configService.getConfigs()
        a.console = '/account'
        a.runtime = 'local'
        res.json(a)
      })
    },
  },
}
