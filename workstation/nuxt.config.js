const { resolve } = require('path')
const lang = require('./src/languages')
const path = require('path')
const fs = require('fs')
const axios = require('axios')
require('dotenv').config()

module.exports = {
  /**
   * from config server
   */
  hooks: {
    render: {
      async before(renderer) {
        if (process.env.NODE_ENV === 'local') return false

        const runtimeConfig = renderer.nuxt.options.publicRuntimeConfig
        const { data } = await axios.get(
          `${process.env.CONFIG_SERVER_URL}/workstation-web/${process.env.NODE_ENV}`,
        )
        const serverConfig = data.propertySources[0].source
        console.log(serverConfig)
        Object.entries(serverConfig).forEach(([key, val]) => {
          runtimeConfig[key] = val
        })
      },
    },
  },
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
  debug: JSON.parse(process.env.NUXT_DEBUG),
  env: {
    NODE_ENV: process.env.NODE_ENV,
  },
  publicRuntimeConfig: {
    VERSION: process.env.npm_package_version || '',
    TARGET_ENV: process.env.TARGET_ENV,
    API_GATEWAY_URL: process.env.API_GATEWAY_URL,
    API_TIMEOUT: parseInt(process.env.API_TIMEOUT, 10),
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
    port: process.env.NUXT_PORT, // default: 3000
    host: process.env.NUXT_HOST, // default: localhost
    https: /(local|develop)/.test(process.env.NODE_ENV) && {
      key: fs.readFileSync(path.resolve(__dirname, 'ssl/server.key')),
      cert: fs.readFileSync(path.resolve(__dirname, 'ssl/server.crt')),
    },
  },
}
