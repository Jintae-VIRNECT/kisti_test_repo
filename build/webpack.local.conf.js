'use strict'
const merge = require('webpack-merge')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const baseWebpackConfig = require('./webpack.base.conf')
const path = require('path')
const fs = require('fs')
const host = '0.0.0.0'
const port = '8886'
const logger = require('@virnect/logger')
const configService = require('../server/config')
const translate = require('../translate/translate')
const stt = require('../translate/stt')
const tts = require('../translate/tts')
// const BundleAnalyzerPlugin = require('webpack-bundle-analyzer')
//   .BundleAnalyzerPlugin

const mode = 'development'

const localWebpackConfig = merge(baseWebpackConfig(mode), {
  mode,
  devtool: 'cheap-module-eval-source-map',
  devServer: {
    https: {
      key: fs.readFileSync(path.join(__dirname, '../ssl/virnect.key')),
      cert: fs.readFileSync(path.join(__dirname, '../ssl/virnect.crt')),
    },
    host,
    port,
    historyApiFallback: {
      rewrites: [
        {
          from: /home(\/.*)?/,
          to: '/remote/index.html',
        },
        {
          from: /service(\/.*)?/,
          to: '/remote/index.html',
        },
        {
          from: /account(\/.*)?/,
          to: '/account/index.html',
        },
        {
          from: /support(\/.*)?/,
          to: '/extra/index.html',
        },
        {
          from: /policy(\/.*)?/,
          to: '/extra/index.html',
        },
        {
          from: /OSS(\/.*)?/,
          to: '/extra/index.html',
        },
        {
          from: /.*/,
          to: '/remote/index.html',
        },
      ],
    },
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
    noInfo: true,
    open: false,
    before: function(app) {
      var bodyParser = require('body-parser')
      configService.init()
      app.use(
        bodyParser.json({
          limit: '50mb',
        }),
      )
      app.get('/healthcheck', function(req, res) {
        res.send(200)
      })

      app.post('/logs', bodyParser.json(), function(req, res) {
        logger.log(req.body.data, 'CONSOLE')
        res.send(true)
      })

      app.get('/configs', bodyParser.json(), function(req, res) {
        const a = configService.getConfigs()
        a.console = '/account'
        a.runtime = 'local'
        res.json(a)
      })

      app.get('/pdf.worker', function(req, res) {
        res.sendFile(path.join(__dirname, '../static/js/pdf.worker.js'))
      })

      app.post('/translate', bodyParser.json(), function(req, res) {
        const text = req.body.text
        const target = req.body.target
        translate.getTranslate(text, target).then(value => {
          res.send(value)
        })
      })

      app.post('/stt', bodyParser.json(), function(req, res) {
        const file = req.body.file
        const lang = req.body.lang
        const rateHertz = req.body.rateHertz
        stt.getStt(file, lang, rateHertz).then(value => {
          console.log(req.body.lang, '::', value)
          res.send(value)
        })
      })

      app.post('/tts', bodyParser.json(), function(req, res) {
        const text = req.body.text
        const lang = req.body.lang
        const voice = req.body.voice
        tts.getTts(text, lang, voice).then(value => {
          // console.log(req.body.text, '::', value)
          res.send(value)
        })
      })
    },
  },

  plugins: [
    new HtmlWebpackPlugin({
      inject: 'body',
      hash: true,
      favicon: './src/assets/favicon.ico',
      template: './src/apps/remote/app.html',
      filename: 'remote/index.html',
      chunks: ['remote'],
    }),
    new HtmlWebpackPlugin({
      inject: 'body',
      hash: true,
      favicon: './src/assets/favicon.ico',
      template: './src/apps/extra/app.html',
      filename: 'extra/index.html',
      chunks: ['extra'],
    }),

    // account
    new HtmlWebpackPlugin({
      inject: 'body',
      hash: true,
      favicon: './src/assets/favicon.ico',
      template: './src/apps/account/app.html',
      filename: 'account/index.html',
      chunks: ['account'],
    }),

    // new BundleAnalyzerPlugin({
    //   analyzerHost: '127.0.0.1',
    //   analyzerPort: 8887,
    // }),
  ],
})

module.exports = localWebpackConfig
