const { resolve } = require('path')
const { VueLoaderPlugin } = require('vue-loader')
const fs = require('fs')

const REPOSITORY_URL = 'https://github.com/virnect-corp/PF-WebWorkStation'
const COMPONENTS_DIR = './src/components'

const sections = fs.readdirSync(COMPONENTS_DIR).map(dir => ({
  name: dir,
  components: `${COMPONENTS_DIR}/${dir}/**/[A-Z]*.vue`,
}))

module.exports = {
  sections,
  ribbon: {
    text: 'Repository',
    url: REPOSITORY_URL,
  },
  webpackConfig: {
    resolve: {
      extensions: ['.js', '.vue'],
      modules: ['node_modules', 'modules'],
      alias: {
        '@': resolve(__dirname, 'src'),
        assets: resolve(__dirname, 'src/assets'),
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
          test: /\.pug$/,
          loader: 'pug-plain-loader',
        },
        {
          test: /\.css$/,
          use: ['vue-style-loader', 'css-loader'],
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
            },
          ],
        },
        {
          test: /assets\/media\/|\.(mp4|ogg|mp3|pdf)$/,
          use: [
            {
              loader: 'file-loader',
            },
          ],
        },
      ],
    },
    plugins: [new VueLoaderPlugin()],
  },
  usageMode: 'expand',
  styleguideDir: 'dist',
}
