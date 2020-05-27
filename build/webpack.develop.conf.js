'use strict'
const merge = require('webpack-merge')
const CleanWebpackPlugin = require('clean-webpack-plugin')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const baseWebpackConfig = require('./webpack.base.conf')
const MiniCssExtractPlugin = require('mini-css-extract-plugin')
const Dotenv = require('dotenv-webpack')

const mode =
  process.env.NODE_ENV === 'develop' ? 'development' : process.env.NODE_ENV

const productionWebpackConfig = merge(baseWebpackConfig(mode), {
  devtool: false,
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
    new MiniCssExtractPlugin({
      filename: './assets/style/[name].[hash:5].css',
    }),
    new Dotenv({
      path: `.env.${process.env.NODE_ENV.trim()}`,
    }),
  ],
  optimization: {},
})

module.exports = productionWebpackConfig
