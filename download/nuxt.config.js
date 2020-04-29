const { resolve } = require('path')
const lang = require('./src/languages')
const dotenv = require('dotenv')
const fs = require('fs')
const filePath = `.env.${process.env.NODE_ENV.trim()}`
const env = dotenv.parse(fs.readFileSync(filePath))
const path = require('path')

module.exports = {
  /*
   ** Headers of the page
   */
  head: {
    htmlAttrs: {
      lang: 'ko',
    },
    title: 'VIRNECT | Download Center',
    meta: [
      { charset: 'utf-8' },
      { name: 'viewport', content: 'width=device-width, initial-scale=1' },
      {
        hid: 'description',
        name: 'description',
        content: 'Virnect Download Center',
      },
    ],
    link: [{ rel: 'icon', type: 'image/x-icon', href: '~assets/favicon.ico' }],
  },
  /**
   * Plugins
   */
  plugins: ['@/plugins/element-ui'],
  modules: [['nuxt-i18n', lang], '@nuxtjs/style-resources'],
  /*
   ** Customize style
   */
  styleResources: {
    scss: [
      resolve(__dirname, '../WC-Modules/src/assets/css/mixin.scss'),
      resolve(__dirname, 'src/assets/css/common.scss'),
    ],
  },
  css: [
    resolve(__dirname, '../WC-Modules/src/assets/css/reset.scss'),
    resolve(__dirname, 'src/assets/css/common.scss'),
  ],
  loading: { color: '#1468e2' },
  /**
   * dir
   */
  srcDir: resolve(__dirname, 'src'),
  modulesDir: [resolve(__dirname, '../WC-Modules/src')],
  /**
   * env
   */
  env: {
    NODE_ENV: JSON.stringify(process.env.NODE_ENV),
    SSL_ENV: JSON.stringify(process.env.SSL_ENV),
    TARGET_ENV: env.TARGET_ENV,
    LOGIN_SITE_URL: env.LOGIN_SITE_URL,
    API_GATEWAY_URL: env.API_GATEWAY_URL,
    API_TIMEOUT: parseInt(env.API_TIMEOUT, 10),
  },
  /**
   * build
   */
  build: {
    extend(config, { isDev, isClient }) {
      config.resolve.alias['WC-Modules'] = resolve(
        __dirname,
        '../WC-Modules/src',
      )
    },
  },
  server: {
    port: env.NUXT_PORT, // default: 3000
    host: env.NUXT_HOST, // default: localhost
    https: {
      key: fs.readFileSync(path.resolve(__dirname, 'ssl/server.key')),
      cert: fs.readFileSync(path.resolve(__dirname, 'ssl/server.crt')),
    },
  },
}
