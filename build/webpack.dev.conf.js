'use strict'
const merge = require('webpack-merge')
const baseWebpackConfig = require('./webpack.base.conf')
const fs = require('fs')
const dotenv = require('dotenv')
const filePath = `.env.${process.env.VIRNECT_ENV.trim()}`
const env = dotenv.parse(fs.readFileSync(filePath))
const configService = require('../configs/runtime')

const devWebpackConfig = merge(baseWebpackConfig, {
	mode: process.env.VIRNECT_ENV === 'local' ? 'development' : 'production',
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

			app.get('/urls', bodyParser.json(), async function(req, res) {
				const a = await configService.getDevUrls()
				res.json(a)
			})
		},
	},
})
module.exports = devWebpackConfig
