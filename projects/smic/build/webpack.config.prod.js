const merge = require('webpack-merge')
const base = require('./webpack.config.base')
const { resolve } = require('path')
const { CleanWebpackPlugin } = require('clean-webpack-plugin')

module.exports = merge(base, {
	mode: 'production',
	output: {
		filename: '[name].bundle.js',
		path: resolve(__dirname, '../dist'),
		// publicPath: '/',
	},
	plugins: [new CleanWebpackPlugin()],
})
