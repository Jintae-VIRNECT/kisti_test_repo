import Vue from 'vue'
import Vuex from 'vuex'

import { auth } from './auth.module'
import isMobile from './mobile'
import { custom } from './custom'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    auth,
    isMobile,
    custom,
  },
  mutations: {},
  actions: {},
  strict: true,
})
