const { resolve } = require('path')
const lang = require('./src/languages')
const dotenv = require('dotenv')
const fs = require('fs')
const filePath = `.env.${process.env.NODE_ENV.trim()}`
const env = dotenv.parse(fs.readFileSync(filePath))

module.exports = {
  /*
   ** Headers of the page
   */
  head: {
    htmlAttrs: {
      lang: 'ko',
    },
    title: 'account',
    meta: [
      { charset: 'utf-8' },
      { name: 'viewport', content: 'width=device-width, initial-scale=1' },
      { hid: 'description', name: 'description', content: 'Virnect Platform' },
    ],
    link: [{ rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' }],
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
      '@/assets/css/_vars.scss',
    ],
  },
  css: [
    resolve(__dirname, '../WC-Modules/src/assets/css/reset.scss'),
    '@/assets/css/global.scss',
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
    USER_API_URL: env.USER_API_URL,
    WORKSPACE_API_URL: env.WORKSPACE_API_URL,
    CONTENT_API_URL: env.CONTENT_API_URL,
    PROCESS_API_URL: env.PROCESS_API_URL,
    LOGIN_SITE_URL: env.LOGIN_SITE_URL,
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
  },
}
