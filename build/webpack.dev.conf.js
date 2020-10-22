'use strict'
const merge = require('webpack-merge')
const baseWebpackConfig = require('./webpack.base.conf')
const fs = require('fs')
const dotenv = require('dotenv')
const filePath = `.env.${process.env.NODE_ENV.trim()}`
const env = dotenv.parse(fs.readFileSync(filePath))
const config = require('../configs/runtime')

const devWebpackConfig = merge(baseWebpackConfig, {
	mode: /local|onpremise/.test(process.env.NODE_ENV)
		? 'development'
		: 'production',
	devtool: 'cheap-module-eval-source-map',
	devServer: {
		host: env.LOCAL_HOST,
		port: env.LOCAL_PORT,
		disableHostCheck: true,
		historyApiFallback: { index: '/app.html' },
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
		before: function(app) {
			let bodyParser = require('body-parser')
			app.use(
				bodyParser.json({
					limit: '50mb',
				}),
			)
			config.init()

			app.get('/urls', bodyParser.json(), function(req, res) {
				res.json(config.urlConfig)
			})
		},
	},
})
module.exports = devWebpackConfig
