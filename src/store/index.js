import Vue from 'vue'
import Vuex from 'vuex'

import { custom } from './custom'
import isMobile from './mobile'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    isMobile,
    custom,
  },
  mutations: {},
  actions: {},
  strict: true,
})
