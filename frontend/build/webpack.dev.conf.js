'use strict'
const merge = require('webpack-merge')
const baseWebpackConfig = require('./webpack.base.conf')

const devWebpackConfig = merge(baseWebpackConfig, {
	mode: 'development',
	devtool: 'cheap-module-eval-source-map',
	devServer: {
		host: '127.0.0.1',
		port: 8888,
		historyApiFallback: { index: '/main/main.html' },
		hot: true,
		noInfo: true,
		// https: true,
		proxy: {
			'/api': {
				target: 'http://127.0.0.1:3334',
				changeOrigin: true,
				pathRewrite: { '^/api': '' },
			},
		},
		open: true,
		openPage: '',
	},
})

module.exports = devWebpackConfig
