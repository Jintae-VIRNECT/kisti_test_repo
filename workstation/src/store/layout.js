import workspaceService from '@/services/workspace'
import OnPremiseSetting from '@/models/workspace/OnPremiseSetting'

const defaultSetting = new OnPremiseSetting()

export default {
  state: () => {
    return { ...defaultSetting }
  },
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
      state.logo = img || defaultSetting.logo
    },
    SET_FAVICON(state, img) {
      state.favicon = img || defaultSetting.favicon

      if (!process.server) {
        console.log('hello')
        const link =
          document.querySelector("link[rel*='icon']") ||
          document.createElement('link')
        link.type = 'image/x-icon'
        link.rel = 'shortcut icon'
        link.href = state.favicon
        document.getElementsByTagName('head')[0].appendChild(link)
      }
    },
  },
  actions: {
    async getWorkspaceSetting({ commit }) {
      const setting = await workspaceService.getWorkspaceSetting()
      commit('SET_TITLE', setting.title)
      commit('SET_LOGO', setting.logo)
      commit('SET_FAVICON', setting.favicon)
      return setting
    },
  },
}
