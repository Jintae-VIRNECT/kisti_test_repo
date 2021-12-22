/* eslint-disable no-unused-vars */

import Vue from 'vue'
import Router from 'routers/account'
import Store from 'stores/remote/store'

import packageInfo from '../../../package.json'
import App from './App.vue'
// import globalMixin from 'mixins/global'

// import i18n from 'plugins/remote/i18n'

// Vue.mixin(globalMixin)

const EventBus = new Vue()
Vue.prototype.$eventBus = EventBus
Vue.prototype.$version = packageInfo.version.replace(/-\w/, '')

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
    `%cVIRNECT Remote DashBoard v${packageInfo.version.replace(/-/, '.')}`,
    'font-size:25px;color:#0f75f5;font-weight:700;font-family:roboto',
  ),
)
