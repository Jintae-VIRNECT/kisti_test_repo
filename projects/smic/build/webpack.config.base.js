const path = require('path')
const merge = require('webpack-merge')
const VueLoaderPlugin = require('vue-loader/lib/plugin')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const { CleanWebpackPlugin } = require('clean-webpack-plugin')

module.exports = (env, options) => {
	const config = {
		entry: path.join(__dirname, '../src/index.js'),
		output: {
			filename: '[name].bundle.js',
			path: path.resolve(__dirname, '../dist'),
			// publicPath: '/',
		},
		node: {
			__dirname: true,
		},
		stats: 'errors-only',
		plugins: [
			new HtmlWebpackPlugin({
				template: './public/index.html',
			}),
			new VueLoaderPlugin(),
			new CleanWebpackPlugin(),
		],
		resolve: {
			extensions: ['.js', '.vue'],
			modules: ['node_modules', 'modules'],
			alias: {
				'@': path.join(__dirname, '../src'),
				assets: path.join(__dirname, '../src/assets'),
			},
		},
		module: {
			rules: [
				{
					test: /\.js$/,
					use: 'babel-loader',
					exclude: /node_modules/,
				},
				{
					test: /\.vue$/,
					use: ['vue-loader'],
				},
				{
					test: /\.css$/,
					use: ['vue-style-loader', 'css-loader'],
				},
				{
					test: /\.pug$/,
					loader: 'pug-plain-loader',
				},
				{
					test: /\.s[a|c]ss$/,
					use: [
						'style-loader', // creates style nodes from JS strings
						'css-loader', // translates CSS into CommonJS
						'sass-loader', // compiles Sass to CSS, using Node Sass by default
					],
					exclude: /node_modules/,
				},
				{
					test: /assets\/image\/|\.(png|jpg|jpeg|gif|svg|svgz)(\?.+)?$/,
					exclude: /favicon\.png$/,
					use: [
						{
							loader: 'url-loader',
							options: {
								limit: 10000,
								esModule: false,
								fallback: 'file-loader',
								name: 'assets/image/[name].[hash:5].[ext]',
							},
						},
					],
				},
				{
					test: /assets\/font\/|\.(eot|ttf|otf|woff|woff2|svg)(\?.+)?$/,
					exclude: [/image/],
					use: [
						{
							loader: 'file-loader',
							options: {
								name: 'assets/font/[name].[hash:5].[ext]',
							},
						},
					],
				},
				{
					test: /assets\/media\/|\.(mp4|ogg|mp3|pdf)$/,
					use: [
						{
							loader: 'file-loader',
							options: {
								name: 'assets/media/[name].[hash:5].[ext]',
							},
						},
					],
				},
			],
		},
	}

	if (options.mode === 'development') {
		const development = require('./webpack.config.dev')
		merge(config, development)
	} else {
		// production
		const production = require('./webpack.config.prod')
		merge(config, production)
	}

	return config
}
