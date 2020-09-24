export default {
  state: () => ({
    lang: 'ko',
  }),
  getters: {
    lang(state) {
      return state.lang
    },
  },
  mutations: {
    CHANGE_LANG(state, str) {
      state.lang = str
    },
  },
  actions: {
    CHANGE_LANG({ commit }, str) {
      commit('CHANGE_LANG', str)
    },
  },
}
