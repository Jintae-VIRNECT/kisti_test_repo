import api from '@/api/gateway'
import Profile from '@/models/Profile'
import Workspace from '@/models/workspace/Workspace'

export default {
  state: () => ({
    isLogin: false,
    myProfile: {},
    myWorkspaces: [],
  }),
  getters: {
    isLogin(state) {
      return state.isLogin
    },
    myProfile(state) {
      return state.myProfile
    },
    myWorkspaces(state) {
      return state.myWorkspaces
    },
  },
  mutations: {
    SET_LOGIN(state, bool) {
      state.isLogin = bool
    },
    SET_MY_PROFILE(state, obj) {
      state.myProfile = obj
    },
    SET_MY_WORKSPACES(state, arr) {
      state.myWorkspaces = arr
    },
  },
  actions: {
    async getAuthInfo({ commit }, params) {
      const data = await api('GET_AUTH_INFO', params)
      commit('SET_MY_PROFILE', new Profile(data.userInfo))
      commit(
        'SET_MY_WORKSPACES',
        data.workspaceInfoList.workspaceList.map(
          workspace => new Workspace(workspace),
        ),
      )
      commit('SET_LOGIN', true)
      return data
    },
  },
}
