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
    title: 'workstation',
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
      '../../../WC-Modules/src/assets/css/mixin.scss',
      '@/assets/css/_vars.scss',
    ],
  },
  css: ['../../../WC-Modules/src/assets/css/reset.scss'],
  loading: { color: '#1b293e' },
  /**
   * dir
   */
  srcDir: 'src/',
  modulesDir: ['../../node_modules'],
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
  },
  /**
   * build
   */
  build: {
    extend(config, { isDev, isClient }) {
      config.resolve.alias['WC-Modules'] = resolve(
        __dirname,
        '../../WC-Modules/src',
      )
    },
  },
}
