'use strict'

const { join, resolve } = require('path')
const MiniCssExtractPlugin = require('mini-css-extract-plugin')
const autoprefixer = require('autoprefixer')

const VueLoaderPlugin = require('vue-loader/lib/plugin')

const config = mode => {
  const isProduction = mode === 'production'

  return {
    entry: {
      dashboard: './src/pages/dashboard/dashboard.js',
      account: './src/pages/account/account.js',
    },
    plugins: [autoprefixer, new VueLoaderPlugin()],
    output: {
      filename: 'assets/scripts/[name].js',
      path: resolve(__dirname, '../dist'),
      publicPath: '/',
      chunkFilename: 'assets/scripts/chunks/[id].js',
    },
    resolve: {
      extensions: ['.js', '.vue', '.json'],
      modules: ['node_modules', 'modules'],
      alias: {
        vue$: 'vue/dist/vue.esm.js',
        '@': join(__dirname, '../'),
        apps: join(__dirname, '../src/apps'),
        assets: join(__dirname, '../src/assets'),
        configs: join(__dirname, '../src/configs'),
        components: join(__dirname, '../src/components'),
        routers: join(__dirname, '../src/routers'),
        languages: join(__dirname, '../src/languages'),
        stores: join(__dirname, '../src/stores'),
        mixins: join(__dirname, '../src/mixins'),
        plugins: join(__dirname, '../src/plugins'),
        api: join(__dirname, '../src/api'),
        utils: join(__dirname, '../src/utils'),
      },
    },
    module: {
      rules: [
        {
          test: /\.vue$/,
          use: 'vue-loader',
        },
        {
          test: /\.js$/,
          use: {
            loader: 'babel-loader',
            options: {
              presets: [['@babel/preset-env', { targets: 'defaults' }]],
            },
          },
          exclude: /node_modules/,
        },
        {
          test: /\.css$/,
          use: [
            isProduction ? MiniCssExtractPlugin.loader : 'style-loader',
            {
              loader: 'css-loader',
              options: {
                sourceMap: true,
              },
            },
          ],
        },
        {
          test: /\.s[a|c]ss$/,
          use: [
            isProduction ? MiniCssExtractPlugin.loader : 'style-loader',
            {
              loader: 'css-loader',
              options: {
                sourceMap: true,
              },
            },
            {
              loader: 'sass-loader',
              options: {
                sourceMap: true,
              },
            },
          ],
        },
        {
          test: /\.html$/,
          use: [
            {
              loader: 'html-loader',
              options: {
                sources: true,
              },
            },
          ],
        },
        {
          test: /assets\/image\/|\.(png|jpg|jpeg|gif|svg|svgz)(\?.+)?$/,
          exclude: /favicon\.ico$/,
          type: 'asset/resource',
        },
        {
          test: /assets\/font\/|\.(eot|ttf|otf|woff|woff2|svg)(\?.+)?$/,
          exclude: [/image/],
          type: 'asset/resource',
        },
        {
          test: /assets\/media\/|\.(mp4|ogg|mp3|pdf)$/,
          type: 'asset/resource',
        },
      ],
    },
    performance: {
      hints: false,
    },
  }
}

module.exports = config
