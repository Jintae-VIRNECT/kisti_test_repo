import 'es6-promise/auto'
import 'babel-polyfill'

import Vue from 'vue'
import Router from 'routers/extra'
import ElementUI from 'element-ui'
//import i18n from 'plugins/service/i18n'
import VueMoment from 'vue-moment'
//import Store from 'stores/admin/store'

import App from './app.vue'
import globalMixin from 'mixins/global'

Vue.use(VueMoment)
//Vue.use(ElementUI, { i18n: (key, value) => i18n.t(key, value) })

Vue.mixin(globalMixin)

export default new Vue({
  el: '#container',
  router: Router,
  //store: Store,
  //i18n,
  render: h => h(App),
})
