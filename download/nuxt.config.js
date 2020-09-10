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
    title: process.env.npm_package_name || '',
    meta: [
      { charset: 'utf-8' },
      { name: 'viewport', content: 'width=device-width, initial-scale=1' },
      {
        hid: 'description',
        name: 'description',
        content: 'Virnect Download Center',
      },
    ],
    link: [{ rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' }],
  },
  /**
   * Plugins
   */
  modules: [['nuxt-i18n', lang], '@nuxtjs/style-resources', '@nuxtjs/axios'],
  buildModules: ['nuxt-composition-api'],
  plugins: ['@/plugins/element-ui', '@/plugins/axios'],
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
  debug: JSON.parse(env.NUXT_DEBUG),
  env: { NODE_ENV: process.env.NODE_ENV },
  publicRuntimeConfig: {
    VERSION: process.env.npm_package_version,
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
  /**
   * custom router
   */
  router: {
    mode: `history`,
    extendRoutes(routes, resolve) {
      routes.push({
        path: '/remote',
        component: resolve(__dirname, 'src/pages/index.vue'),
        name: 'Remote',
      })
      routes.push({
        path: '/make',
        component: resolve(__dirname, 'src/pages/index.vue'),
        name: 'Make',
      })
      routes.push({
        path: '/view',
        component: resolve(__dirname, 'src/pages/index.vue'),
        name: 'View',
      })
    },
  },
}
