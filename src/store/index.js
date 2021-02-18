import Vue from 'vue'
import Vuex from 'vuex'

import { auth } from './auth.module'
import { lang } from './lang'
import { custom } from './custom'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    auth,
    lang,
    custom,
  },
  mutations: {},
  actions: {},
  strict: true,
})
