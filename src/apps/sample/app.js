import 'es6-promise/auto'
import 'babel-polyfill'

import Vue from 'vue'
import ElementUI from 'element-ui'
import moment from 'moment'
import VueMoment from 'vue-moment'
import Router from 'routers/sample'
import Vue2Scrollbar from 'plugins/remote/scrollbar'

import App from './app.vue'

Vue.use(ElementUI)

Vue.use(VueMoment, {
  moment,
})
Vue.use(Vue2Scrollbar)

const EventBus = new Vue()
Vue.prototype.$eventBus = EventBus

export default new Vue({
  el: '#container',
  router: Router,
  render: h => h(App),
})
