const { resolve } = require('path')
const lang = require('./src/languages')
const path = require('path')
const fs = require('fs')
const axios = require('axios')
const dotenv = require('dotenv')

const env = dotenv.parse(fs.readFileSync('.env.local'))
for (const key in env) {
  process.env[key] = env[key]
}

module.exports = async () => {
  /**
   * from config server
   */
  if (process.env.VIRNECT_ENV !== 'local') {
    const { data } = await axios.get(
      `${process.env.CONFIG_SERVER}/account-web/${process.env.VIRNECT_ENV}`,
    )
    const serverConfig = data.propertySources[0].source
    console.log(serverConfig)
    for (const key in serverConfig) {
      process.env[key] = serverConfig[key]
    }
  }
  return {
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
          content: 'Virnect Platform',
        },
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
    debug: process.env.NUXT_DEBUG,
    devtools: process.env.NUXT_DEBUG,
    env: {
      NODE_ENV: process.env.NODE_ENV,
    },
    publicRuntimeConfig: {
      VERSION: process.env.npm_package_version || '',
      VIRNECT_ENV: process.env.NODE_ENV,
      API_GATEWAY_URL: process.env.API_GATEWAY_URL,
      API_TIMEOUT: process.env.API_TIMEOUT,
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
      port: process.env.NUXT_PORT,
      host: process.env.NUXT_HOST,
      https: /(local|develop)/.test(process.env.VIRNECT_ENV) && {
        key: fs.readFileSync(path.resolve(__dirname, 'ssl/server.key')),
        cert: fs.readFileSync(path.resolve(__dirname, 'ssl/server.crt')),
      },
    },
  }
}
