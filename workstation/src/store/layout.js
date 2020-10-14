export default {
  state: () => ({
    title: 'VIRNECT',
    logo: require('assets/images/logo/logo-gnb-ci.png'),
    favicon: require('assets/images/logo/favicon.png'),
  }),
  getters: {
    title(state) {
      return state.title
    },
    logo(state) {
      return state.logo
    },
    favicon(state) {
      return state.favicon
    },
  },
  mutations: {
    SET_TITLE(state, str) {
      state.title = str
    },
    SET_LOGO(state, img) {
      state.logo = img
    },
    SET_FAVICON(state, img) {
      state.favicon = img
    },
  },
  actions: {},
}
