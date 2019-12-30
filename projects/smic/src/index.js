import 'es6-promise/auto'
import 'babel-polyfill'
import Vue from 'vue'
import ElementUI from 'element-ui'
import VueSweetalert2 from 'vue-sweetalert2'

import i18n from '@/i18n'
import App from '@/App.vue'
import router from '@/router'
import store from '@/store'

import 'sweetalert2/dist/sweetalert2.min.css'
import 'element-ui/lib/theme-chalk/index.css'
import '@/assets/style/common.scss'
import '@/assets/style/index.scss'
import '@/assets/style/admin.scss'

Vue.use(ElementUI, {
	i18n: (key, value) => i18n.t(key, value),
})
Vue.use(VueSweetalert2)

Vue.config.productionTip = false

new Vue({
	router,
	store,
	i18n,
	render: h => h(App),
}).$mount('#app')
