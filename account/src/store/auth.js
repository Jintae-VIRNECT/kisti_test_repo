import { api } from '@/plugins/axios'
import Profile from '@/models/profile/Profile'
import Workspace from '@/models/workspace/Workspace'

export default {
  state: () => ({
    isLogin: false,
    authenticated: false,
    myProfile: {},
    myWorkspaces: [],
  }),
  getters: {
    isLogin(state) {
      return state.isLogin
    },
    authenticated(state) {
      return state.authenticated
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
    SET_AUTH(state, bool) {
      state.authenticated = bool
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
        data.workspaceInfoList.map(workspace => new Workspace(workspace)),
      )
      commit('SET_LOGIN', true)
    },
  },
}
