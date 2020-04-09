export default {
  state: () => ({
    isLogin: false,
    authenticated: false,
    myProfile: {},
  }),
  getters: {
    isLogin(state) {
      return state.isLogin
    },
    authenticated(state) {
      return state.authenticated
    },
    myProfile(state) {
      return state.myProfile
    },
  },
  mutations: {
    SET_LOGIN(state, bool) {
      state.isLogin = bool
    },
    SET_AUTH(state, bool) {
      state.authenticated = bool
    },
    SET_MY_PROFILE(state, obj) {
      state.myProfile = obj
    },
  },
  actions: {},
}
