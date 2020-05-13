'use strict'
const merge = require('webpack-merge')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const baseWebpackConfig = require('./webpack.base.conf')
const path = require('path')
const fs = require('fs')
const host = 'local.virnect.com'
const port = '8886'
const logger = require('../server/logger')
const Dotenv = require('dotenv-webpack')
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
      '/api': {
        target: 'https://develop.virnect.com:8073',
        headers: {
          'Access-Control-Allow-Origin': '*',
        },
        pathRewrite: { '^/api': '' },
        secure: false,
        changeOrigin: true,
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

    new Dotenv({
      path: `.env.${process.env.NODE_ENV.trim()}`,
    }),

    // new BundleAnalyzerPlugin({
    //     analyzerHost: '127.0.0.1',
    //     analyzerPort: 8887
    // })
  ],
})

module.exports = localWebpackConfig
