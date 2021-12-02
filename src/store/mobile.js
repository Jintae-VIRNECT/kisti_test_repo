export default {
  state: {
    isMobile: false,
  },
  getters: {
    isMobile(state) {
      return state.isMobile
    },
  },
  mutations: {
    IS_MOBILE(state, value) {
      state.isMobile = value
    },
  },
}
