'use strict'
const webpack = require('webpack')
const { merge } = require('webpack-merge')
const { CleanWebpackPlugin } = require('clean-webpack-plugin')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const baseWebpackConfig = require('./webpack.base.conf')
const MiniCssExtractPlugin = require('mini-css-extract-plugin')
const CompressionPlugin = require('compression-webpack-plugin')

const mode = process.env.NODE_ENV === 'develop' ? 'development' : 'production'

const productionWebpackConfig = merge(baseWebpackConfig(mode), {
  devtool: false,
  mode,
  plugins: [
    new webpack.ProvidePlugin({
      process: 'process/browser',
    }),
    new CleanWebpackPlugin(),
    new HtmlWebpackPlugin({
      inject: 'body',
      hash: true,
      template: './public/index.html',
      filename: 'index.html',
      chunks: ['dashboard'],
    }),

    new MiniCssExtractPlugin({
      filename: './assets/style/[name].[fullhash:5].css',
    }),
    new CompressionPlugin({
      filename: '[path][base].gz[query]',
      algorithm: 'gzip',
      test: /\.(js|html)$/,
      minRatio: 0.8,
    }),
  ],
  optimization: {},
})

module.exports = productionWebpackConfig
