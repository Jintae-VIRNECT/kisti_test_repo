const path = require('path')

module.exports = {
  pages: {
    dashboard: {
      entry: 'src/pages/dashboard/dashboard.js',
      template: 'public/dashboard.html',
      filename: 'dashboard.html',
      title: 'dashboard',
    },
    index: {
      entry: 'src/pages/index/remote.js',
      template: 'public/index.html',
      filename: 'index.html',
      title: 'remote',
    },
  },
  configureWebpack: {
    resolve: {
      modules: ['node_modules', 'modules'],
      alias: {
        '@': path.join(__dirname, '../src'),
      },
    },
  },
}
