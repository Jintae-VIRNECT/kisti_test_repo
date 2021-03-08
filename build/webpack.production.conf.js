'use strict'
const merge = require('webpack-merge')
const CleanWebpackPlugin = require('clean-webpack-plugin')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const baseWebpackConfig = require('./webpack.base.conf')
const MiniCssExtractPlugin = require('mini-css-extract-plugin')
// const CompressionPlugin = require('compression-webpack-plugin')

const mode = process.env.NODE_ENV === 'develop' ? 'development' : 'production'

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
    new MiniCssExtractPlugin({
      filename: './assets/style/[name].[hash:5].css',
    }),
    // new CompressionPlugin({
    //   filename: '[path].gz[query]',
    //   algorithm: 'gzip',
    //   test: /\.(js|html)$/,
    //   minRatio: 0.8,
    // }),
  ],
  optimization: {},
})

module.exports = productionWebpackConfig
