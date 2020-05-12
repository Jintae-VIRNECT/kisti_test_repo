'use strict'
const webpack = require('webpack')
const merge = require('webpack-merge')
const CleanWebpackPlugin = require('clean-webpack-plugin')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const baseWebpackConfig = require('./webpack.base.conf')
const MiniCssExtractPlugin = require('mini-css-extract-plugin')
const Dotenv = require('dotenv-webpack')

const mode = 'development'

const developWebpackConfig = merge(baseWebpackConfig(mode), {
  devtool: false,
  mode,
  plugins: [
    new CleanWebpackPlugin('../dist/*', {
      allowExternal: true,
    }),
    new webpack.DefinePlugin({
      'process.env': {
        NODE_ENV: '"develop"',
      },
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
      template: './src/apps/sample/app.html',
      filename: 'sample/index.html',
      chunks: ['sample'],
    }),
    new HtmlWebpackPlugin({
      inject: 'body',
      hash: true,
      favicon: './src/assets/favicon.ico',
      template: './src/apps/extra/app.html',
      filename: 'extra/index.html',
      chunks: ['extra'],
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

module.exports = developWebpackConfig
