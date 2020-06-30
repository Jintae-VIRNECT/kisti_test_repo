import 'es6-promise/auto'
import 'babel-polyfill'

import Vue from 'vue'
import globalMixin from 'mixins/global'
import Router from 'routers/sample'
import Vue2Scrollbar from 'plugins/remote/scrollbar'
import DayJS from 'plugins/remote/dayjs'

import App from './app.vue'

Vue.use(DayJS)

Vue.use(Vue2Scrollbar)
Vue.mixin(globalMixin)

const EventBus = new Vue()
Vue.prototype.$eventBus = EventBus

export default new Vue({
  el: '#container',
  router: Router,
  render: h => h(App),
})
