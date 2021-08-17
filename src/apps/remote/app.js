import 'es6-promise/auto'
import '@babel/polyfill'

import Vue from 'vue'
import Router from 'routers/remote'
import Store from 'stores/remote/store'
import * as Sentry from '@sentry/browser'
import { Vue as VueIntegration } from '@sentry/integrations'

import App from './app.vue'
import globalMixin from 'mixins/global'

import i18n from 'plugins/remote/i18n'
import Vue2Scrollbar from 'plugins/remote/scrollbar'
import VueToasted from 'plugins/remote/toasted'
import Alarm from 'plugins/remote/alarm'
import DayJS from 'plugins/remote/dayjs'

import call from 'plugins/remote/call'

import PUSH from 'plugins/remote/push'

import { version } from '@/package.json'
// const version = '2.0'

import { logger, debug } from 'utils/logger'

import '../../registerServiceWorker'

Vue.use(DayJS)
Vue.use(PUSH)
Vue.use(VueToasted)
Vue.use(Alarm)

Vue.mixin(globalMixin)
Vue.use(Vue2Scrollbar)

Vue.use(call, { Store })

Vue.prototype.logger = logger
Vue.prototype.debug = debug

if ('remote.virnect.com' === location.host) {
  Sentry.init({
    dsn:
      'https://18c249a3501c43bbaf223c6b53f598ab@o280606.ingest.sentry.io/5354647',
    integrations: [
      new VueIntegration({
        Vue,
        attachProps: true,
      }),
    ],
  })
}

const EventBus = new Vue()
Vue.prototype.$eventBus = EventBus
Vue.prototype.$version = version.replace(/-\w/, '')

const app = new Vue({
  el: '#container',
  router: Router,
  store: Store,
  i18n,
  render: h => h(App),
})

window.vue = app

export default app

setTimeout(
  console.log.bind(
    console,
    `%cVIRNECT Remote v${version.replace(/-/, '.')}`,
    'font-size:25px;color:#0f75f5;font-weight:700;font-family:roboto',
  ),
)
