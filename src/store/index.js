import Vue from 'vue'
import Vuex from 'vuex'

import { auth } from './auth.module'
import { lang } from './lang'

Vue.use(Vuex)

export default new Vuex.Store({
	modules: {
		auth,
		lang,
	},
	mutations: {},
	actions: {},
	strict: true,
})
