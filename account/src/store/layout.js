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
