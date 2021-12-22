'use strict'
const { merge } = require('webpack-merge')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const baseWebpackConfig = require('./webpack.base.conf')
const path = require('path')
const fs = require('fs')
const host = '0.0.0.0'
const port = '9989'
const logger = require('@virnect/logger')
const configService = require('../server/config')
var bodyParser = require('body-parser')

const mode = 'development'

const localWebpackConfig = merge(baseWebpackConfig(mode), {
  mode,
  devtool: 'eval-cheap-module-source-map',
  devServer: {
    https: {
      key: fs.readFileSync(path.join(__dirname, '../ssl/virnect.key')),
      cert: fs.readFileSync(path.join(__dirname, '../ssl/virnect.crt')),
    },
    host,
    port,
    proxy: {
      '/api': {
        target: 'https://192.168.6.3:8073',
        headers: {
          'Access-Control-Allow-Origin': '*',
        },
        secure: false,
        changeOrigin: true,
      },
    },
    // historyApiFallback: true,

    open: false,
    onBeforeSetupMiddleware: function (devServer) {
      var express = require('express')
      configService.init()
      devServer.app.use(
        express.json({
          limit: '50mb',
        }),
      )
      devServer.app.get('/healthcheck', function (req, res) {
        res.send(200)
      })

      devServer.app.post('/logs', bodyParser.json(), function (req, res) {
        logger.log(req.body.data, 'CONSOLE')
        res.send(true)
      })

      devServer.app.get('/configs', bodyParser.json(), function (req, res) {
        const a = configService.getConfigs()
        a.console = '/account'
        a.runtime = 'local'
        res.json(a)
      })
    },
  },

  plugins: [
    new HtmlWebpackPlugin({
      inject: 'body',
      hash: true,
      template: './public/index.html',
      filename: 'index.html',
      chunks: ['dashboard'],
    }),

    // account
    new HtmlWebpackPlugin({
      inject: 'body',
      hash: true,
      template: './public/account.html',
      filename: 'account/index.html',
      chunks: ['account'],
    }),
  ],
})

module.exports = localWebpackConfig
