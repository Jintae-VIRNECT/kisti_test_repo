const { resolve } = require('path')
const lang = require('./src/languages')
const path = require('path')
const fs = require('fs')

module.exports = async () => {
  const env = await require('./config')()
  console.log(env)

  return {
    /*
     ** Headers of the page
     */
    head: {
      htmlAttrs: {
        lang: 'ko',
      },
      title: env.PROJECT_NAME,
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
        '@virnect/WC-Modules/src/assets/css/mixin.scss',
        resolve(__dirname, 'src/assets/css/common.scss'),
      ],
    },
    css: [
      '@virnect/WC-Modules/src/assets/css/reset.scss',
      resolve(__dirname, 'src/assets/css/common.scss'),
    ],
    loading: { color: '#1468e2' },
    /**
     * dir
     */
    srcDir: resolve(__dirname, 'src'),
    modulesDir: ['@virnect/WC-Modules/src'],
    /**
     * env
     */
    debug: env.NUXT_DEBUG,
    devtools: env.NUXT_DEBUG,
    env: {
      NODE_ENV: env.NODE_ENV,
    },
    publicRuntimeConfig: {
      VERSION: env.PROJECT_VERSION,
      VIRNECT_ENV: env.VIRNECT_ENV,
      API_GATEWAY_URL: env.URLS.api,
      API_TIMEOUT: env.API_TIMEOUT,
      URLS: env.URLS,
    },
    /**
     * build
     */
    build: {
      extend(config, { isDev, isClient }) {
        config.resolve.alias['WC-Modules'] = '@virnect/WC-Modules/src'
      },
    },
    server: {
      port: env.NUXT_PORT,
      host: env.NUXT_HOST,
      https: env.SSL_ENV && {
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
          path: '/',
          component: resolve(__dirname, 'src/pages/index.vue'),
          alias: ["/remote", "/make", "/view", "/track"],
        })
      },
    },
  }
}
