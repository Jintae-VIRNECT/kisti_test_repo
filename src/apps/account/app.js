import 'es6-promise/auto'
import '@babel/polyfill'

import Vue from 'vue'

import App from './app.vue'

export default new Vue({
  el: '#container',
  render: h => h(App),
})
