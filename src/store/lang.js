import Cookies from 'js-cookie'
export const lang = {
  state: {
    lang: null,
  },
  getters: {
    lang(state) {
      return state.lang
    },
  },
  mutations: {
    async CHANGE_LANG(state, locale) {
      state.lang = locale
      if (locale === 'ko') {
        document.documentElement.lang = 'ko'
      } else {
        document.documentElement.lang = 'en'
      }
    },
  },
  actions: {
    async CHANGE_LANG({ commit }, lang) {
      const cookieOption = {
        domain:
          location.hostname.split('.').length === 3
            ? location.hostname.replace(/.*?\./, '')
            : location.hostname,
      }
      // let lang = Cookies.get('lang', cookieOption)
      if (!lang) {
        if (navigator.language.substr(0, 2) !== 'ko') {
          lang = 'en'
        } else if (navigator.userLanguage.substr(0, 2) !== 'ko') {
          lang = 'en'
        } else if (navigator.systemLanguage.substr(0, 2) !== 'ko') {
          lang = 'en'
        } else {
          lang = 'ko'
        }

        // if (navigator.language != null) {
        //   lang = navigator.language.substr(0, 2)
        // } else if (navigator.userLanguage != null) {
        //   lang = navigator.userLanguage.substr(0, 2)
        // } else if (navigator.systemLanguage != null) {
        //   lang = navigator.systemLanguage.substr(0, 2)
        // } else {
        //   lang = 'ko'
        // }
      }
      // console.log(lang)
      Cookies.set('lang', lang, cookieOption)
      commit('CHANGE_LANG', lang)
    },
  },
}
