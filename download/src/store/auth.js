export default {
  state: () => ({
    isLogin: false,
    myProfile: {},
  }),
  getters: {
    isLogin(state) {
      return state.isLogin
    },
    myProfile(state) {
      return state.myProfile
    },
  },
  mutations: {
    SET_LOGIN(state, bool) {
      state.isLogin = bool
    },
    SET_MY_PROFILE(state, obj) {
      state.myProfile = obj
    },
  },
  actions: {
    async getAuthInfo({ commit }, params) {
      const data = await this.$api('GET_AUTH_INFO', params)
      commit('SET_MY_PROFILE', data.userInfo)
      commit('SET_LOGIN', true)
      return data
    },
  },
}
