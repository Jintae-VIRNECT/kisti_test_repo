'use strict'
const webpack = require('webpack')
const { merge } = require('webpack-merge')
const CleanWebpackPlugin = require('clean-webpack-plugin')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const baseWebpackConfig = require('./webpack.base.conf')

const mode =
  process.env.NODE_ENV === 'develop' ? 'development' : process.env.NODE_ENV

const productionWebpackConfig = merge(baseWebpackConfig(mode), {
  devtool: 'eval-cheap-source-map',
  mode,
  plugins: [
    new CleanWebpackPlugin('../dist/*', {
      allowExternal: true,
    }),
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
    new HtmlWebpackPlugin({
      inject: 'body',
      hash: true,
      favicon: './src/assets/favicon.ico',
      template: './src/apps/account/app.html',
      filename: 'account/index.html',
      chunks: ['account'],
    }),
    // test
    new HtmlWebpackPlugin({
      inject: 'body',
      hash: true,
      favicon: './src/assets/favicon.ico',
      template: './src/apps/test/app.html',
      filename: 'test/index.html',
      chunks: ['test'],
    }),
    new webpack.DefinePlugin({
      GOOGLE_MAP_API: '"AIzaSyCh1LbSwSgaSxpBb7PJ9Z_pLLd4gxd6Uz4"',
    }),
  ],
  optimization: {},
})

module.exports = productionWebpackConfig
