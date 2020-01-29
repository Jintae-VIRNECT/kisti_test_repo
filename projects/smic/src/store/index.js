import Vue from 'vue'
import Vuex from 'vuex'
import modules from './modules'
import createPersistedState from 'vuex-persistedstate'
import SecureLS from 'secure-ls'

const ls = new SecureLS({ isCompression: false })
const secureSetting =
  process.env.NODE_ENV === 'production'
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
  plugins: [createPersistedState(secureSetting)],
})
