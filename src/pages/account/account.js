/* eslint-disable no-unused-vars */

import Vue from 'vue'
import Router from 'routers/account'
import Store from 'stores/remote/store'

import App from './App.vue'
// import globalMixin from 'mixins/global'

// import i18n from 'plugins/remote/i18n'

// Vue.mixin(globalMixin)

const EventBus = new Vue()
Vue.prototype.$eventBus = EventBus
Vue.prototype.version = '2.0.0'

export default new Vue({
  el: '#container',
  router: Router,
  store: Store,
  // i18n,
  render: h => h(App),
})

setTimeout(
  console.log.bind(
    console,
    `%c VIRNECT Remote Service `,
    'padding:4px 18px;background:linear-gradient(to right, #0064ff, #6700ff);font-size:32px;color:#fff;border-radius:15px',
  ),
)
