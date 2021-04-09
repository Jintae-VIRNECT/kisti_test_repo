import 'es6-promise/auto'
import Vue from 'vue'
import VueI18n from 'vue-i18n'
import router from '@/router/index'
import { sync } from 'vuex-router-sync'
import store from '@/store'
import VeeValidate from 'vee-validate'
import App from '@/App.vue'
import messages from 'languages'
import { version } from 'root/package.json'
import '@virnect/ui-assets/css/base.css'
import ElementUI from 'element-ui'

import locale from 'element-ui/src/locale/lang/ko'
import elementLocale from 'element-ui/src/locale'
elementLocale.use(locale)

import VueQrcodeReader from 'vue-qrcode-reader'

import 'element/row.css'
import 'element/input.css'
import 'element/checkbox.css'
import 'element/col.css'
import 'element/button.css'
import 'element/icon.css'
import 'element/select.css'
import 'element/radio.css'
import 'element/date-picker.css'
import 'element/message.css'
import 'element/message-box.css'
import 'element/dialog.css'

import 'assets/css/common.scss'
import 'assets/css/_override.scss'

sync(store, router)
Vue.config.productionTip = false

Vue.use(VeeValidate)
Vue.use(ElementUI, {
  i18n: (key, value) => i18n.t(key, value),
})
Vue.use(VueQrcodeReader)

Vue.use(VueI18n)
const i18n = new VueI18n({
  locale: 'ko',
  fallbackLocale: process.env.VIRNECT_ENV === 'production' ? 'ko' : null,
  messages: messages,
})

const versionCss = 'font-size: 2rem; color: #297af3;'
console.log('%cVIRNECT Console %s', versionCss, version)

new Vue({
  el: '#app',
  router,
  store,
  i18n,
  render: h => h(App),
})
