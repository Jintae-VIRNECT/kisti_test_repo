'use strict'
const webpack = require('webpack')
const merge = require('webpack-merge')
const CleanWebpackPlugin = require('clean-webpack-plugin')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const baseWebpackConfig = require('./webpack.base.conf')
const MiniCssExtractPlugin = require('mini-css-extract-plugin')

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
      template: './src/apps/service/app.html',
      filename: 'service/index.html',
      chunks: ['service'],
    }),
    new HtmlWebpackPlugin({
      inject: 'body',
      hash: true,
      favicon: './src/assets/favicon.ico',
      template: './src/apps/test/app.html',
      filename: 'test/index.html',
      chunks: ['test'],
    }),
    new MiniCssExtractPlugin({
      filename: './assets/style/[name].[hash:5].css',
    }),
    // new OptimizeCSSPlugin({
    //     cssProcessor: require('cssnano'),
    //     cssProcessorPluginOptions: {
    //       preset: ['default', { discardComments: { removeAll: true } }],
    //     },
    //     canPrint: true
    // })
  ],
  optimization: {
    // minimizer: [
    //     new TerserPlugin({
    //         sourceMap: false,
    //         terserOptions: {
    //             compress: {
    //                 drop_console: true
    //             }
    //         }
    //     })
    // ],
    // splitChunks: {
    //     chunks: 'initial',
    //     maxSize: 8000000,
    //     automaticNameDelimiter: '~',
    //     cacheGroups: {
    //         vendors: {
    //             filename: '[name].js',
    //             test: /[\\/]node_modules[\\/]/,
    //             priority: -10
    //         }
    //     }
    // }
  },
})

module.exports = developWebpackConfig
