import ElementUI from 'element-ui'
import Vue from 'vue'
import VueI18n from 'vue-i18n'
import router from '@/router'
import { sync } from 'vuex-router-sync'
import store from '@/store'
import BootstrapVue from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap-vue/dist/bootstrap-vue.min.css'
// import 'element-ui/lib/theme-chalk/index.css'
import VeeValidate from 'vee-validate'
import { library } from '@fortawesome/fontawesome-svg-core'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import {
	faHome,
	faUser,
	faUserPlus,
	faSignInAlt,
	faSignOutAlt,
} from '@fortawesome/free-solid-svg-icons'
import App from '@/App.vue'
import messages from 'languages'
import 'element/row.css'
import 'element/input.css'
import 'element/checkbox.css'
import 'element/col.css'
import 'element/button.css'
import 'element/icon.css'


import 'assets/css/common.scss'

library.add(faHome, faUser, faUserPlus, faSignInAlt, faSignOutAlt)

sync(store, router)
Vue.config.productionTip = false
Vue.use(BootstrapVue)

Vue.use(VueI18n)
const i18n = new VueI18n({
	locale: 'ko',
	fallbackLocale: 'en',
	messages: messages,
})

Vue.use(VeeValidate)
Vue.component('font-awesome-icon', FontAwesomeIcon)
Vue.use(ElementUI)

new Vue({
	el: '#app',
	router,
	store,
	i18n,
	render: h => h(App),
})
