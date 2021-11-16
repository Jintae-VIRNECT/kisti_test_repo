const { resolve } = require('path')
const lang = require('./src/languages')
const path = require('path')
const fs = require('fs')
const logger = require('@virnect/logger')

module.exports = async () => {
  const env = await require('./config')()
  logger.info('server is running...', 'LISTENING')
  logger.ipInfo(`${env.NUXT_PORT}`, 'LISTENING')
  logger.info(`VIRNECT_ENV: ${env.VIRNECT_ENV}`, 'LISTENING')
  logger.info(`SSL_ENV: ${env.SSL_ENV}`, 'LISTENING')
  Object.entries(env.URLS).forEach(([key, val]) =>
    logger.info(`${key}: ${val}`, 'LISTENING'),
  )

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
          content: 'Download Center',
        },
      ],
      link: [{ rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' }],
    },
    /**
     * Plugins
     */
    modules: [
      'nuxt-helmet',
      ['nuxt-i18n', lang],
      '@nuxtjs/style-resources',
      '@nuxtjs/axios',
    ],
    plugins: [
      '@/plugins/element-ui',
      '@/plugins/axios',
      '@/plugins/context',
      '@/plugins/virnectComponents',
    ],
    /*
     ** Customize style
     */
    styleResources: {
      scss: [
        '@virnect/ui-assets/stylesheets/abstracts/_vars.scss',
        '@virnect/ui-assets/stylesheets/abstracts/_mixins.scss',
        resolve(__dirname, 'src/assets/css/_vars.scss'),
      ],
    },
    css: [
      '@virnect/ui-assets/css/base.old.css',
      resolve(__dirname, 'src/assets/css/common.scss'),
    ],
    loading: { color: '#1468e2' },
    /**
     * dir
     */
    srcDir: resolve(__dirname, 'src'),
    components: [
      { path: '~/components' },
      // { prefix: 'virnect', path: '@virnect/components' },
    ],
    alias: {
      '~@virnect': resolve(__dirname, './node_modules/@virnect'),
    },
    build: {
      transpile: ['@virnect/platform-auth', '@virnect/components'],
    },
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
          alias: ['/remote', '/make', '/view', '/track'],
        })
      },
    },
  }
}
