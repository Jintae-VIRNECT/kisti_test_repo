const merge = require('webpack-merge')
const base = require('./webpack.config.base')
const { resolve } = require('path')

module.exports = merge(base, {
	mode: 'development',
	output: {
		filename: '[name].bundle.js',
		path: resolve(__dirname, '../dist'),
		publicPath: '/',
	},
	devtool: 'inline-source-map',
	devServer: {
		historyApiFallback: { index: '/' },
		hot: true,
	},
})
