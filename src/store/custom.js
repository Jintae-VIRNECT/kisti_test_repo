import WorkspaceService from 'service/workspace-service'

export const custom = {
	state: {
		customInfo: null,
	},
	getters: {
		customInfo(state) {
			return state.customInfo
		},
	},
	mutations: {
		async SET_CUSTOM(state, custom) {
			state.customInfo = custom
		},
	},
	actions: {
		async SET_CUSTOM({ commit }) {
			const { data } = await WorkspaceService.customConfig()

			if (window.env === 'onpremise') {
				console.log(`<----------replace proxy url`)
				const URLS = window.urls
				const proxyUrl = url => {
					if ('minio' in URLS && url !== null) {
						return url.replace(/^((http[s]?|ftp):\/\/)([^/]+)/, URLS['minio'])
					} else {
						return url
					}
				}
				for (const [key, url] of Object.entries(data)) {
					data[key] = proxyUrl(url)
				}
				console.log(data)
				console.log(`replace proxy url---------->`)
			}

			document.title = `${data.workspaceTitle} Login Center`
			let favicon = document.querySelector("link[rel*='icon']")
			favicon.href = data.favicon

			let custom = {}
			custom = {
				default: data.defaultLogo,
				white: data.whiteLogo,
				grey: data.greyLogo,
				title: data.workspaceTitle,
			}
			commit('SET_CUSTOM', custom)
		},
	},
}
