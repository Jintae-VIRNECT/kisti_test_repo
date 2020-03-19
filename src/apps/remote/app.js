import 'es6-promise/auto'
import 'babel-polyfill'

import Vue from 'vue'
import ElementUI from 'element-ui'
import moment from 'moment'
import VueMoment from 'vue-moment'
import Router from 'routers/remote'
import Store from 'stores/remote/store'

import App from './app.vue'
import globalMixin from 'mixins/global'
import i18n from 'plugins/remote/i18n'
import Vue2Scrollbar from 'plugins/remote/scrollbar'

import openvidu from 'plugins/remote/openvidu'

Vue.use(ElementUI, {
  i18n: (key, value) => i18n.t(key, value),
})

Vue.use(VueMoment, {
  moment,
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
