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
		CHANGE_LANG(state, locale) {
			state.lang = locale
			if (locale === 'ko') {
				document.documentElement.lang = 'ko'
			} else {
				document.documentElement.lang = 'en'
			}
		},
	},
	actions: {
		async CHANGE_LANG({ commit }) {
			let lang = Cookies.get('lang')
			if (!lang) {
				if (navigator.language != null) {
					lang = navigator.language.substr(0, 2)
				} else if (navigator.userLanguage != null) {
					lang = navigator.userLanguage.substr(0, 2)
				} else if (navigator.systemLanguage != null) {
					lang = navigator.systemLanguage.substr(0, 2)
				} else {
					lang = 'ko'
				}
			}
			commit('CHANGE_LANG', lang)
		},
	},
}
