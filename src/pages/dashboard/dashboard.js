/* eslint-disable no-unused-vars */

import Vue from 'vue'
import Router from '../../routers/dashboard'
import App from './App.vue'
import Vue2Scrollbar from 'plugins/remote/scrollbar'

Vue.use(Vue2Scrollbar)

Vue.config.productionTip = false

const EventBus = new Vue()
Vue.prototype.$eventBus = EventBus

const app = new Vue({
  router: Router,
  render: h => h(App),
}).$mount('#app')
