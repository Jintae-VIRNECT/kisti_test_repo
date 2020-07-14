import 'es6-promise/auto'
import 'babel-polyfill'

import Vue from 'vue'
import Router from 'routers/remote'
import Store from 'stores/remote/store'

import App from './app.vue'
import globalMixin from 'mixins/global'

import i18n from 'plugins/remote/i18n'
import Vue2Scrollbar from 'plugins/remote/scrollbar'
import VueToasted from 'plugins/remote/toasted'
import Alarm from 'plugins/remote/alarm'
import DayJS from 'plugins/remote/dayjs'

import call from 'plugins/remote/call'

import PUSH from 'plugins/remote/push'

import { localStorage } from 'utils/storage'
import { version } from '@/package.json'

Vue.use(DayJS)
Vue.use(PUSH)
Vue.use(VueToasted)
Vue.use(Alarm)

Vue.mixin(globalMixin)
Vue.use(Vue2Scrollbar)

Vue.use(call, { Store })

Vue.prototype.$localStorage = localStorage

const EventBus = new Vue()
Vue.prototype.$eventBus = EventBus
Vue.prototype.$version = version

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
    `%cVIRNECT Remote v${version}`,
    'font-size:25px;color:#0f75f5;font-weight:700;font-family:roboto',
  ),
)
