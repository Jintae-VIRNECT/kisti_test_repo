const path = require('path')

module.exports = {
  pages: {
    dashboard: {
      entry: 'src/pages/dashboard/dashboard.js',
      template: 'public/dashboard.html',
      filename: 'dashboard.html',
      title: 'dashboard',
      chunks: ['chunk-vendors', 'chunk-common', 'index'],
    },
    index: {
      entry: 'src/pages/index/remote.js',
      template: 'public/index.html',
      filename: 'index.html',
      title: 'remote',
      chunks: ['chunk-vendors', 'chunk-common', 'index'],
    },
  },

  configureWebpack: {
    resolve: {
      modules: ['node_modules'],
      // modules: ['node_modules', path.resolve(__dirname, './src')],
      alias: {
        '@': path.join(__dirname, '/src'),
        components: path.join(__dirname, '/src/components'),
        assets: path.join(__dirname, '/src/assets'),
      },
    },
  },
}
