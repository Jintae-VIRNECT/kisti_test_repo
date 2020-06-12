'use strict'

const { join, resolve } = require('path')
const MiniCssExtractPlugin = require('mini-css-extract-plugin')
const autoprefixer = require('autoprefixer')

const config = mode => {
  const isProduction = mode === 'production'

  return {
    entry: {
      remote: './src/apps/remote/app.js',
      extra: './src/apps/extra/app.js',
      account: './src/apps/account/app.js',
      sample: './src/apps/sample/app.js',
    },
    output: {
      filename: 'assets/scripts/[name].js',
      path: resolve(__dirname, '../dist'),
      publicPath: '/',
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
        static: join(__dirname, '../static'),
      },
    },
    module: {
      rules: [
        {
          test: /\.vue$/,
          use: ['vue-loader'],
        },
        {
          test: /\.js$/,
          use: 'babel-loader',
          exclude: /node_modules/,
        },
        {
          test: /\.css$/,
          use: [
            isProduction ? MiniCssExtractPlugin.loader : 'style-loader',
            {
              loader: 'css-loader',
              options: {
                sourceMap: !isProduction,
                plugins: () => [autoprefixer()],
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
                sourceMap: !isProduction,
                plugins: () => [autoprefixer()],
              },
            },
            {
              loader: 'sass-loader',
              options: {
                sourceMap: !isProduction,
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
                root: resolve(__dirname, '../src/assets'),
                attrs: ['img:src', 'img:srcset'],
              },
            },
          ],
        },
        {
          test: /assets\/image\/|\.(png|jpg|jpeg|gif|svg|svgz)(\?.+)?$/,
          exclude: /favicon\.png$/,
          use: [
            {
              loader: 'url-loader',
              options: {
                limit: 10000,
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
        // {
        //   test: /static\/js\/|\.(js)$/,
        //   use: [
        //     {
        //       loader: 'file-loader',
        //       options: {
        //         name: 'static/js/[name].[hash:5].[ext]',
        //       },
        //     },
        //   ],
        // },
      ],
    },
    performance: {
      hints: false,
    },
  }
}

module.exports = config
