import auth from 'WC-Modules/javascript/api/virnectPlatform/virnectPlatformAuth'

export default {
  state: () => ({
    auth: {
      env: '',
      urls: {},
      myInfo: {},
    },
  }),
  getters: {
    auth(state) {
      return state.auth
    },
  },
  mutations: {
    SET_AUTH(state, obj) {
      state.auth = obj
    },
  },
  actions: {
    async getAuth({ commit }, env) {
      commit('SET_AUTH', await auth.init({ env }))
    },
  },
}
