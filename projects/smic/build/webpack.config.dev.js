const webpack = require('webpack')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const merge = require('webpack-merge')
const base = require('./webpack.config.base')

module.exports = merge(base, {
	mode: 'development',
	stats: 'errors-only',
	plugins: [
		new webpack.HotModuleReplacementPlugin(),
		new HtmlWebpackPlugin({
			title: 'Development',
			showErrors: true,
		}),
	],
	devtool: 'inline-source-map',
	devServer: {
		hot: true,
		host: '0.0.0.0',
		contentBase: './dist',
		stats: {
			color: true,
		},
	},
})
