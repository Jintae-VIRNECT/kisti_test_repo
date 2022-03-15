import workspaceService from '@/services/workspace'
import WorkspaceSetting from '@/models/workspace/WorkspaceSetting'
import { context } from '@/plugins/context'

const defaultSetting = new WorkspaceSetting()

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
    return {
      ...defaultSetting,
    }
  },
  getters: {
    title(state) {
      return state.title
    },
    logo(state) {
      return state.logo
    },
    androidType1Logo(state) {
      return state.androidType1Logo
    },
    androidType2Logo(state) {
      return state.androidType2Logo
    },
    hololens2Logo(state) {
      return state.hololens2Logo
    },
    whiteLogo(state) {
      return state.whiteLogo
    },
    favicon(state) {
      return state.favicon
    },
  },
  mutations: {
    SET_TITLE(state, str) {
      state.title = str
    },
    SET_LOGO(state, { logo, whiteLogo }) {
      state.logo = logo || defaultSetting.logo
      state.whiteLogo = whiteLogo || defaultSetting.whiteLogo
    },
    SET_APP_LOGO(state, { androidType1Logo, androidType2Logo, hololens2Logo }) {
      state.androidType1Logo =
        androidType1Logo || defaultSetting.androidType1Logo
      state.androidType2Logo =
        androidType2Logo || defaultSetting.androidType2Logo
      state.hololens2Logo = hololens2Logo || defaultSetting.hololens2Logo
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
      commit('SET_FAVICON', setting.favicon)
      commit('SET_LOGO', {
        logo: setting.logo,
        whiteLogo: setting.whiteLogo,
      })
      commit('SET_APP_LOGO', {
        androidType1Logo: setting.androidType1Logo,
        androidType2Logo: setting.androidType2Logo,
        hololens2Logo: setting.hololens2Logo,
      })
      return setting
    },
  },
}
