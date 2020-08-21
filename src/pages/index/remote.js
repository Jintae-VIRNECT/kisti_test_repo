/* eslint-disable no-unused-vars */

import Vue from 'vue'
import VueRouter from 'vue-router'
import App from './App.vue'
import Vue2Scrollbar from 'plugins/remote/scrollbar'

Vue.use(Vue2Scrollbar)

// Vue.use(VueRouter)
Vue.config.productionTip = false

const EventBus = new Vue()
Vue.prototype.$eventBus = EventBus

const app = new Vue({
  // router,
  render: h => h(App),
}).$mount('#app')
