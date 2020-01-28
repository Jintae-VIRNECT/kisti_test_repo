const { join } = require('path')
const { DefinePlugin } = require('webpack')
const VueLoaderPlugin = require('vue-loader/lib/plugin')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const dotenv = require('dotenv')
const fs = require('fs')
const filePath = `.env.${process.env.NODE_ENV.trim()}`
const env = dotenv.parse(fs.readFileSync(filePath))

module.exports = {
  node: {
    fs: 'empty',
  },
  entry: join(__dirname, '../src/index.js'),
  plugins: [
    new HtmlWebpackPlugin({
      template: join(__dirname, '../public/index.html'),
      favicon: join(__dirname, '../public/favicon.ico'),
    }),
    new VueLoaderPlugin(),
    new DefinePlugin({
      'process.env': {
        NODE_ENV: JSON.stringify(process.env.NODE_ENV),
        SSL_ENV: JSON.stringify(process.env.SSL_ENV),
        BASE_URL: JSON.stringify(env.BASE_URL),
        USER_API_URL: JSON.stringify(env.USER_API_URL),
      },
    }),
  ],
  resolve: {
    extensions: ['.js', '.vue'],
    modules: ['node_modules', 'modules'],
    alias: {
      '@': join(__dirname, '../src'),
      assets: join(__dirname, '../src/assets'),
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
        use: [
          {
            loader: 'url-loader',
            options: {
              limit: 10000,
              esModule: false,
              fallback: 'file-loader',
              // name: 'assets/image/[name].[hash:5].[ext]',
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
            // options: {
            // 	name: 'assets/font/[name].[hash:5].[ext]',
            // },
          },
        ],
      },
      {
        test: /assets\/media\/|\.(mp4|ogg|mp3|pdf)$/,
        use: [
          {
            loader: 'file-loader',
            // options: {
            // 	name: 'assets/media/[name].[hash:5].[ext]',
            // },
          },
        ],
      },
    ],
  },
}
