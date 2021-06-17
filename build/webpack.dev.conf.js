'use strict'
const merge = require('webpack-merge')
const baseWebpackConfig = require('./webpack.base.conf')
const fs = require('fs')
const dotenv = require('dotenv')
const filePath = `.env.${process.env.NODE_ENV.trim()}`
const env = dotenv.parse(fs.readFileSync(filePath))
const config = require('../configs/runtime')

const devWebpackConfig = merge(baseWebpackConfig, {
  mode: 'development',
  devtool: 'cheap-module-eval-source-map',
  devServer: {
    host: '0.0.0.0',
    port: env.LOCAL_PORT,
    disableHostCheck: true,
    historyApiFallback: { index: '/apps/main/app.html' },
    hot: true,
    noInfo: true,
    https: true,
    open: false,
    before: function (app) {
      let bodyParser = require('body-parser')
      app.use(
        bodyParser.json({
          limit: '50mb',
        }),
      )
      config.init()

      app.get('/urls', bodyParser.json(), function (req, res) {
        res.json(config.urlConfig)
      })
    },
  },
})
module.exports = devWebpackConfig
