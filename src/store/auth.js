import { api } from '@/plugins/axios'
import { context } from '@/plugins/context'
import Profile from '@/models/Profile'
import Workspace from '@/models/workspace/Workspace'
import Cookies from 'js-cookie'
import auth from '@virnect/platform-auth'

export default {
  state: () => ({
    auth: {
      env: '',
      urls: {},
      myInfo: {},
    },
    myProfile: {},
    myWorkspaces: [],
    activeWorkspace: {},
  }),
  getters: {
    auth(state) {
      return state.auth
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
    SET_AUTH(state, obj) {
      state.auth = obj
    },
    async LOGOUT() {
      await auth.logout()
      location.reload()
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
    async getAuth({ commit }) {
      commit(
        'SET_AUTH',
        await auth.init({
          env: context.$config.VIRNECT_ENV,
          urls: context.$url,
          timeout: context.$config.API_TIMEOUT,
        }),
      )
    },
    async getAuthInfo({ commit }, params) {
      const data = await api('GET_AUTH_INFO', params)
      commit('SET_MY_PROFILE', new Profile(data.userInfo))
      commit(
        'SET_MY_WORKSPACES',
        data.workspaceInfoList.map(workspace => new Workspace(workspace)),
      )
      return data
    },
  },
}
