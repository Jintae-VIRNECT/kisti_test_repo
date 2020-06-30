import 'es6-promise/auto'
import 'babel-polyfill'

import Vue from 'vue'
import Router from 'routers/extra'
import i18n from 'plugins/remote/i18n'
import Store from 'stores/remote/store'

import App from './app.vue'
import globalMixin from 'mixins/global'

Vue.mixin(globalMixin)

export default new Vue({
  el: '#container',
  router: Router,
  store: Store,
  i18n,
  render: h => h(App),
})
