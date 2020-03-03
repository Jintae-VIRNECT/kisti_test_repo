const { resolve } = require('path')
const { VueLoaderPlugin } = require('vue-loader')

const docSiteUrl =
  process.env.DEPLOY_PRIME_URL || 'https://vue-styleguidist.github.io'

module.exports = {
  components: './src/components/**/[A-Z]*.vue',
  ribbon: {
    text: 'Back to examples',
    url: `${docSiteUrl}/Examples.html`,
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
