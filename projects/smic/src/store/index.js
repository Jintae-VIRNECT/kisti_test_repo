import Vue from 'vue'
import Vuex from 'vuex'
import modules from './modules'
import createPersistedState from 'vuex-persistedstate'
import SecureLS from 'secure-ls'

const ls = new SecureLS({ isCompression: false })
const secureSetting =
  process.NODE_ENV === 'production'
    ? {
        storage: {
          getItem: key => ls.get(key),
          setItem: (key, value) => ls.set(key, value),
          removeItem: key => ls.remove(key),
        },
      }
    : {}

Vue.use(Vuex)

export default new Vuex.Store({
  modules,
  mutations: {},
  actions: {},
  // strict: true, // 시연용이라 주석처리함.
  plugins: [createPersistedState(secureSetting)],
})
