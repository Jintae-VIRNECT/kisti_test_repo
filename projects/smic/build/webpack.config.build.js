const merge = require('webpack-merge')
const base = require('./webpack.config.base')
const { CleanWebpackPlugin } = require('clean-webpack-plugin')
const { resolve } = require('path')

module.exports = merge(base, {
	mode: 'production',
	output: {
		filename: '[name].bundle.js',
		path: resolve(__dirname, '../dist'),
		publicPath: '/dist/',
	},
	plugins: [new CleanWebpackPlugin()],
})
