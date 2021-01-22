import workspaceService from '@/services/workspace'

export default {
  state: () => ({
    plansInfo: {
      storage: {},
      viewCount: {},
      callTime: {},
      remote: {},
      make: {},
      view: {},
    },
  }),
  getters: {
    plansInfo(state) {
      return state.plansInfo
    },
  },
  mutations: {
    SET_PLANS_INFO(state, obj) {
      state.plansInfo = obj
    },
  },
  actions: {
    async getPlansInfo({ commit }) {
      const plansInfo = await workspaceService.getWorkspacePlansInfo()
      commit('SET_PLANS_INFO', plansInfo)
      return plansInfo
    },
  },
}
