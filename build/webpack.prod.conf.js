'use strict'
const merge = require('webpack-merge')
const baseWebpackConfig = require('./webpack.base.conf')
const OptimizeCSSPlugin = require('optimize-css-assets-webpack-plugin')
const prodWebpackConfig = merge(baseWebpackConfig, {
	devtool: false,
	plugins: [new OptimizeCSSPlugin()],
})

module.exports = prodWebpackConfig
