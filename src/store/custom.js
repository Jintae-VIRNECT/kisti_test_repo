import WorkspaceService from 'service/workspace-service'
export const custom = {
	state: {
		logoImg: null,
	},
	getters: {
		logoImg(state) {
			return state.logoImg
		},
	},
	mutations: {
		async SET_CUSTOM(state, custom) {
			state.logoImg = custom
		},
	},
	actions: {
		async SET_CUSTOM({ commit }) {
			const { data } = await WorkspaceService.customConfig()
			document.title = `${data.workspaceTitle} Login Center`
			let favicon = document.querySelector("link[rel*='icon']")
			favicon.href = data.favicon

			let custom = {}
			custom = {
				default: data.defaultLogo,
				white: data.whiteLogo,
				grey: data.greyLogo,
			}
			commit('SET_CUSTOM', custom)
		},
	},
}
