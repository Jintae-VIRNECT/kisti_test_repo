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
  /*
   ** Customize the progress bar color
   */
  loading: { color: '#3B8070' },
  /**
   * Plugins
   */
  plugins: ['@/plugins/element-ui'],
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
    USER_API_URL: 'http://192.168.6.3:8081',
    WORKSPACE_API_URL: 'http://192.168.6.3:8082',
    CONTENT_API_URL: 'http://192.168.6.3:8083',
  },
}
