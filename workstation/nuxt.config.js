const { resolve } = require('path')
const lang = require('./src/languages')
const dotenv = require('dotenv')
const fs = require('fs')
const filePath = `.env.${process.env.NODE_ENV.trim()}`
const env = dotenv.parse(fs.readFileSync(filePath))
const path = require('path')
const packageJson = JSON.parse(
  fs.readFileSync(path.resolve(__dirname, 'package.json')),
)

module.exports = {
  /*
   ** Headers of the page
   */
  head: {
    htmlAttrs: {
      lang: 'ko',
    },
    title: 'Workstation',
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
  modules: [['nuxt-i18n', lang], '@nuxtjs/style-resources', '@nuxtjs/axios'],
  plugins: ['@/plugins/element-ui', '@/plugins/axios', '@/plugins/context'],
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
  debug: JSON.parse(env.NUXT_DEBUG),
  env: {
    NODE_ENV: process.env.NODE_ENV,
  },
  publicRuntimeConfig: {
    VERSION: packageJson.version,
    TARGET_ENV: env.TARGET_ENV,
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
    https: /(local|develop)/.test(env.TARGET_ENV) && {
      key: fs.readFileSync(path.resolve(__dirname, 'ssl/server.key')),
      cert: fs.readFileSync(path.resolve(__dirname, 'ssl/server.crt')),
    },
  },
}
