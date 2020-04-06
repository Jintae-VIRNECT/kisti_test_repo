export default {
  state: () => ({
    authenticated: false,
  }),
  getters: {
    authenticated(state) {
      return state.authenticated
    },
  },
  mutations: {
    SET_AUTH(state, bool) {
      state.authenticated = bool
    },
  },
  actions: {},
}
