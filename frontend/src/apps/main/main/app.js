import ElementUI from 'element-ui'
import Vue from 'vue'
import VueI18n from 'vue-i18n'
import router from '@/router/index'
import { sync } from 'vuex-router-sync'
import store from '@/store'
import VeeValidate from 'vee-validate'
import App from '@/App.vue'
import messages from 'languages'

import locale from 'element-ui/lib/locale/lang/ko'
import elementLocale from 'element-ui/lib/locale'
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

sync(store, router)
Vue.config.productionTip = false

Vue.use(VeeValidate)
// Vue.use(ElementUI, { locale })
Vue.use(ElementUI, {
	messages: (key, value) => i18n.t(key, value),
})
Vue.use(VueQrcodeReader)

Vue.use(VueI18n)
const i18n = new VueI18n({
	locale: 'ko',
	fallbackLocale: 'ko',
	messages: messages,
})

new Vue({
	el: '#app',
	router,
	store,
	i18n,
	render: h => h(App),
})
