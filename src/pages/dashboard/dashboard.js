/* eslint-disable no-unused-vars */

import Vue from 'vue'
import Router from 'routers/dashboard'
import App from './App.vue'
import Vue2Scrollbar from 'plugins/remote/scrollbar'
import DayJS from 'plugins/remote/dayjs'
import Datepicker from 'plugins/remote/datepicker'
import VDatepicker from 'plugins/remote/vdatepicker'

import i18n from 'plugins/remote/i18n'
import globalMixin from 'mixins/global'

// import { version } from '@/package.json'
import { version } from '../../../package.json'
import { logger, debug } from 'utils/logger'

import Store from 'stores/remote/store'
import VueToasted from 'plugins/remote/toasted'

Vue.use(Vue2Scrollbar)
Vue.use(DayJS)
Vue.use(Datepicker)
Vue.use(VueToasted)
Vue.use(VDatepicker)
Vue.mixin(globalMixin)

Vue.config.productionTip = false

const EventBus = new Vue()
Vue.prototype.$eventBus = EventBus
Vue.prototype.$version = version
Vue.prototype.logger = logger
Vue.prototype.debug = debug

const app = new Vue({
  router: Router,
  store: Store,
  render: h => h(App),
  i18n,
}).$mount('#container')

window.vue = app
