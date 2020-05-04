'use strict'

const { join, resolve, posix } = require('path')
const webpack = require('webpack')
const glob = require('glob')

const ExtractTextPlugin = require('extract-text-webpack-plugin')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const CopyWebpackPlugin = require('copy-webpack-plugin')
const OptimizeCSSPlugin = require('optimize-css-assets-webpack-plugin')
const Dotenv = require('dotenv-webpack')

const extractCSS = new ExtractTextPlugin({
	filename: getPath => {
		return getPath('[name].css').replace('css/js', 'css')
	},
	allChunks: true,
})

const extractSASS = new ExtractTextPlugin({
	filename: getPath => {
		return getPath('[name].css').replace('css/js', 'css')
	},
	allChunks: true,
})

const entries = {}
const chunks = []
const htmlWebpackPluginArray = []
glob.sync('./src/apps/**/app.js').forEach(path => {
	const chunk = path.split('./src/apps/')[1].split('/app.js')[0]
	entries[chunk] = path
	chunks.push(chunk)

	const htmlConf = {
		filename: resolve(
			__dirname,
			'../../src/main/resources/templates/index.html',
		),
		template: path.replace(/.js/g, '.html'),
		inject: 'body',
		favicon: './src/assets/favicon.ico',
		hash: true,
		chunks: ['commons', chunk],
	}
	htmlWebpackPluginArray.push(new HtmlWebpackPlugin(htmlConf))
})

const styleLoaderOptions = {
	loader: 'style-loader',
	options: {
		sourceMap: true,
	},
}
const cssOptions = [
	{ loader: 'css-loader', options: { sourceMap: true } },
	{ loader: 'sass-loader', options: { sourceMap: true } },
]

const sassOptions = [
	...cssOptions,
	{
		loader: 'sass-loader',
		options: {
			sourceMap: true,
		},
	},
]

const config = {
	entry: entries,
	output: {
		path: resolve(__dirname, '../../src/main/resources/static'),
		filename: posix.join('js/[name].js'),
		publicPath: '/',
	},
	resolve: {
		extensions: ['.js', '.vue'],
		alias: {
			'WC-Modules': resolve(__dirname, '../../WC-Modules/src'),
			'@': resolve(__dirname, '../src'),
			api: join(__dirname, '../src/api'),
			apps: join(__dirname, '../src/apps'),
			assets: join(__dirname, '../src/assets'),
			components: join(__dirname, '../src/components'),
			model: join(__dirname, '../src/model'),
			service: join(__dirname, '../src/service'),
			mixins: join(__dirname, '../src/mixins'),
			languages: join(__dirname, '../src/languages'),
			// config: join(__dirname, '../src/config'),
			routers: join(__dirname, '../src/routers'),
			stores: join(__dirname, '../src/stores'),
			element: join(__dirname, '../theme/'),
		},
	},
	module: {
		rules: [
			{
				test: /\.vue$/,
				loader: 'vue-loader',
				options: {
					loaders: {
						css: ['css-hot-loader'].concat(
							ExtractTextPlugin.extract({
								use: cssOptions,
								fallback: styleLoaderOptions,
							}),
						),
						scss: ['css-hot-loader'].concat(
							ExtractTextPlugin.extract({
								use: sassOptions,
								fallback: styleLoaderOptions,
							}),
						),
					},
				},
			},
			{
				test: /\.js$/,
				use: 'babel-loader',
				exclude: /node_modules/,
			},
			{
				test: /\.css$/,
				use: ['css-hot-loader'].concat(
					ExtractTextPlugin.extract({
						use: cssOptions,
						fallback: styleLoaderOptions,
					}),
				),
			},
			{
				test: /\.scss$/,
				use: ['css-hot-loader'].concat(
					ExtractTextPlugin.extract({
						use: sassOptions,
						fallback: styleLoaderOptions,
					}),
				),
			},
			{
				test: /\.html$/,
				use: [
					{
						loader: 'html-loader',
						options: {
							root: resolve(__dirname, 'src'),
							attrs: ['img:src', 'link:href'],
						},
					},
				],
			},
			{
				test: /\.(png|jpg|jpeg|gif|eot|otf|ttf|woff|woff2|svg|svgz|ico|pdf|rss|xml|txt)(\?.+)?$/,
				exclude: /favicon\.png$/,
				use: [
					{
						loader: 'url-loader',
						options: {
							limit: 10000,
							name: 'assets/[name].[hash:7].[ext]',
						},
					},
				],
			},
			{
				test: /\.(mp4|ogg|mp3|pdf)$/,
				use: [
					{
						loader: 'file-loader',
						options: {
							name: 'assets/[name].[hash:7].[ext]',
						},
					},
				],
			},
		],
	},
	optimization: {
		splitChunks: {
			cacheGroups: {
				commons: {
					chunks: 'initial',
					minChunks: 3,
					name: 'commons',
					enforce: true,
				},
			},
		},
	},
	performance: {
		hints: false,
	},
	plugins: [
		new webpack.optimize.ModuleConcatenationPlugin(),
		extractSASS,
		extractCSS,
		new CopyWebpackPlugin(
			[
				{
					from: './src/assets/',
					to: '',
				},
			],
			{
				ignore: ['*.gitkeep'],
			},
		),
		new OptimizeCSSPlugin(),
		new Dotenv({
			path: `.env.${process.env.NODE_ENV.trim()}`,
		}),
	],
	node: {
		net: 'empty',
		tls: 'empty',
		dns: 'empty',
		dgram: 'empty',
		fs: 'empty',
	},
	devtool: '#source-map',
}
config.plugins = [...config.plugins, ...htmlWebpackPluginArray]

module.exports = config
