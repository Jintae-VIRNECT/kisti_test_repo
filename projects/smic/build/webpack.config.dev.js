const webpack = require('webpack')
const HtmlWebpackPlugin = require('html-webpack-plugin')

module.exports = {
	mode: 'development',
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
}
