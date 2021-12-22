'use strict'
const { merge } = require('webpack-merge')
const { CleanWebpackPlugin } = require('clean-webpack-plugin')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const baseWebpackConfig = require('./webpack.base.conf')
const MiniCssExtractPlugin = require('mini-css-extract-plugin')

const mode =
  process.env.NODE_ENV === 'develop' ? 'development' : process.env.NODE_ENV

const productionWebpackConfig = merge(baseWebpackConfig(mode), {
  devtool: 'eval-cheap-source-map',
  mode,
  plugins: [
    new CleanWebpackPlugin(),
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

    new MiniCssExtractPlugin({
      filename: './assets/style/[name].[fullhash:5].css',
    }),
  ],
  optimization: {},
})

module.exports = productionWebpackConfig
