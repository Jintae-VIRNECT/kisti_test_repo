module.exports = {
  pages: {
    index: {
      entry: 'src/pages/index/index.js',
      template: 'public/index.html',
      filename: 'index.html',
      title: 'index',
    },
    dashboard: {
      entry: 'src/pages/dashboard/dashboard.js',
      template: 'public/dashboard.html',
      filename: 'dashboard.html',
      title: 'dashboard',
    },
    remote: {
      entry: 'src/pages/remote/remote.js',
      template: 'public/remote.html',
      filename: 'remote.html',
      title: 'remote',
    },
  },
}
