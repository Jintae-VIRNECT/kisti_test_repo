import 'es6-promise/auto'
import 'babel-polyfill'

import Vue from 'vue'
import Router from 'routers/remote'
import Store from 'stores/remote/store'

import App from './app.vue'
import globalMixin from 'mixins/global'

import ElementUI from 'element-ui'
import i18n from 'plugins/remote/i18n'
import Vue2Scrollbar from 'plugins/remote/scrollbar'
import SweetAlert from 'plugins/remote/sweetalert2'
import VueToasted from 'plugins/remote/toasted'
import DayJS from 'plugins/remote/dayjs'

import openvidu from 'plugins/remote/openvidu'

Vue.use(DayJS)
Vue.use(VueToasted)
Vue.use(SweetAlert)

Vue.use(ElementUI, {
  i18n: (key, value) => i18n.t(key, value),
})

Vue.mixin(globalMixin)
Vue.use(Vue2Scrollbar)

Vue.use(openvidu, { Store })

const EventBus = new Vue()
Vue.prototype.$eventBus = EventBus
Vue.prototype.version = '2.0.0'

export default new Vue({
  el: '#container',
  router: Router,
  store: Store,
  i18n,
  render: h => h(App),
})

setTimeout(
  console.log.bind(
    console,
    `%c VIRNECT Remote Service `,
    'padding:4px 18px;background:linear-gradient(to right, #0064ff, #6700ff);font-size:32px;color:#fff;border-radius:15px',
  ),
)
