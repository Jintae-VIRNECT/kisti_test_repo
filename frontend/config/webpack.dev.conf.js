'use strict'
const merge = require('webpack-merge')
const baseWebpackConfig = require('./webpack.base.conf')
const fs = require('fs')
const dotenv = require('dotenv')
const filePath = `.env.${process.env.NODE_ENV.trim()}`
const env = dotenv.parse(fs.readFileSync(filePath))

const devWebpackConfig = merge(baseWebpackConfig, {
	mode: process.env.NODE_ENV === 'local' ? 'development' : 'production',
	devtool: 'cheap-module-eval-source-map',
	devServer: {
		host: env.LOCAL_HOST,
		port: env.LOCAL_PORT,
		disableHostCheck: true,
		historyApiFallback: { index: '/main/main.html' },
		hot: true,
		noInfo: true,
		https: true,
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
