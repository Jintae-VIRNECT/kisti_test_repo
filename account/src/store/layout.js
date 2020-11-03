import workspaceService from '@/services/workspace'
import OnPremiseSetting from '@/models/workspace/OnPremiseSetting'
import { context } from '@/plugins/context'

const defaultSetting = new OnPremiseSetting()

/**
 * cdn files filter, onpremise minio proxy url
 * @param {string} url
 */
function cdn(url) {
  if (url && 'minio' in context.$url) {
    url = url.replace(/^((http[s]?|ftp):\/\/)([^/]+)/, context.$url['minio'])
  }
  return url
}

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
      for (let prop in setting) {
        setting[prop] = cdn(setting[prop])
      }
      commit('SET_TITLE', setting.title)
      commit('SET_LOGO', setting.logo)
      commit('SET_FAVICON', setting.favicon)
      return setting
    },
  },
}
