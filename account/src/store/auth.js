export default {
  state: () => ({
    authenticated: false,
    myProfile: {},
  }),
  getters: {
    authenticated(state) {
      return state.authenticated
    },
    myProfile(state) {
      return state.myProfile
    },
  },
  mutations: {
    SET_AUTH(state, bool) {
      state.authenticated = bool
    },
    SET_MY_PROFILE(state, obj) {
      state.myProfile = obj
    },
  },
  actions: {},
}
