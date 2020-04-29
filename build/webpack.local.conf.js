'use strict'
const merge = require('webpack-merge')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const baseWebpackConfig = require('./webpack.base.conf')
const path = require('path')
const fs = require('fs')
const host = '0.0.0.0'
const port = process.env.port
const logger = require('../server/logger')
// const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin

const mode = 'development'

const localWebpackConfig = merge(baseWebpackConfig(mode), {
  mode,
  devtool: 'cheap-module-eval-source-map',
  devServer: {
    https: {
      key: fs.readFileSync(path.join(__dirname, '../ssl/server.key')),
      cert: fs.readFileSync(path.join(__dirname, '../ssl/server.crt')),
    },
    host,
    port,
    historyApiFallback: {
      rewrites: [
        {
          from: /workspace(\/.*)?/,
          to: '/remote/index.html',
        },
        {
          from: /sample(\/.*)?/,
          to: '/sample/index.html',
        },
        {
          from: /support(\/.*)?/,
          to: '/extra/index.html',
        },
        {
          from: /.*/,
          to: '/remote/index.html',
        },
      ],
    },
    proxy: {
      '/admin': {
        target: 'http://192.168.6.3:8081',
        headers: {
          'Access-Control-Allow-Origin': '*',
        },
      },
      '/auth': {
        target: 'http://192.168.6.3:8321',
        headers: {
          'Access-Control-Allow-Origin': '*',
        },
      },
    },
    noInfo: true,
    open: false,
    before: function(app) {
      var bodyParser = require('body-parser')
      app.use(
        bodyParser.json({
          limit: '50mb',
        }),
      )

      app.post('/logs', bodyParser.json(), function(req, res) {
        logger.log(req.body.data, 'CONSOLE')
        res.send(true)
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

    // sample
    new HtmlWebpackPlugin({
      inject: 'body',
      hash: true,
      favicon: './src/assets/favicon.ico',
      template: './src/apps/sample/app.html',
      filename: 'sample/index.html',
      chunks: ['sample'],
    }),

    // new BundleAnalyzerPlugin({
    //     analyzerHost: '127.0.0.1',
    //     analyzerPort: 8887
    // })
  ],
})

module.exports = localWebpackConfig
