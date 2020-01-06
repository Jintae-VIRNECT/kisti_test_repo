import 'es6-promise/auto'
import 'babel-polyfill'
import Vue from 'vue'
import ElementUI from 'element-ui'
import VueSweetalert2 from 'vue-sweetalert2'
import locale from '@/plugins/i18n-ko'

import App from '@/App.vue'
import router from '@/router'
import store from '@/store'

import 'element-ui/lib/theme-chalk/index.css'
import '@/assets/style/common.scss'
import '@/assets/style/index.scss'
import '@/assets/style/admin.scss'
import 'sweetalert2/dist/sweetalert2.min.css'
import 'billboard.js/dist/billboard.min.css'

import ThePageHeader from '@/components/common/ThePageHeader'

Vue.use(ElementUI, { locale })
Vue.use(VueSweetalert2)

Vue.component('the-page-header', ThePageHeader)

Vue.config.productionTip = false

new Vue({
	router,
	store,
	render: h => h(App),
}).$mount('#app')
