import { api } from '@/plugins/axios'
import Profile from '@/models/Profile'
import Workspace from '@/models/workspace/Workspace'
import Cookies from 'js-cookie'

export default {
  state: () => ({
    isLogin: false,
    myProfile: {},
    myWorkspaces: [],
    activeWorkspace: {},
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
    activeWorkspace(state) {
      return state.activeWorkspace
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
    SET_ACTIVE_WORKSPACE(state, workspaceId) {
      state.activeWorkspace = state.myWorkspaces.find(
        workspace => workspace.uuid === workspaceId,
      )
      Cookies.set('activeWorkspace', workspaceId, { expires: 7 })
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
      return data
    },
  },
}
