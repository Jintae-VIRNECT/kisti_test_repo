/* eslint-disable no-unused-vars */

import Vue from 'vue'
import VueRouter from 'vue-router'
import App from './App.vue'

// Vue.use(VueRouter)
Vue.config.productionTip = false

const app = new Vue({
  // router,
  render: h => h(App),
}).$mount('#app')
