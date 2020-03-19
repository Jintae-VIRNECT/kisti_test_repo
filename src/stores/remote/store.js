import Vue from 'vue'
import Vuex from 'vuex'
import modules from './modules'
import getters from './getters'
import actions from './actions'

Vue.use(Vuex)

export default new Vuex.Store({
  modules,
  getters,
  actions,
  strict: true,
})
