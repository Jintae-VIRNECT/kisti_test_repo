const path = require('path')

module.exports = {
  pages: {
    index: {
      entry: 'src/pages/dashboard/dashboard.js',
      template: 'public/index.html',
      filename: 'index.html',
      title: 'Remote - Admin',
      chunks: ['chunk-vendors', 'chunk-common', 'index'],
    },
  },

  configureWebpack: {
    resolve: {
      modules: ['node_modules', 'modules'],
      alias: {
        '@': path.join(__dirname, '../'),
        components: path.join(__dirname, '/src/components'),
        assets: path.join(__dirname, '/src/assets'),
        plugins: path.join(__dirname, '/src/plugins'),
        api: path.join(__dirname, '/src/api'),
        utils: path.join(__dirname, '/src/utils'),
        mixins: path.join(__dirname, '/src/mixins'),
        configs: path.join(__dirname, '/src/configs'),
        stores: path.join(__dirname, '/src/stores'),
      },
    },
  },
}
