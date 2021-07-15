import { api } from '@/plugins/axios'
import { context } from '@/plugins/context'
import Profile from '@/models/profile/Profile'
import Workspace from '@/models/workspace/Workspace'
import auth from '@virnect/platform-auth'

export default {
  state: () => ({
    auth: {
      env: '',
      urls: {},
      myInfo: {},
    },
    authenticated: false,
    myProfile: {},
    myWorkspaces: [],
  }),
  getters: {
    auth(state) {
      return state.auth
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
    LOGOUT() {
      auth.logout()
      location.reload()
    },
    SET_AUTH(state, obj) {
      state.auth = obj
    },
    SET_AUTHENTICATED(state, bool) {
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
    async getAuth({ commit }) {
      const a = await auth.init({
        env: context.$config.VIRNECT_ENV,
        urls: context.$url,
        timeout: context.$config.API_TIMEOUT,
      })
      commit('SET_AUTH', a)
      commit('SET_MY_PROFILE', new Profile(a.myInfo, a.accessToken))
    },
    async getAuthInfo({ commit }, params) {
      const data = await api('GET_AUTH_INFO', params)
      const accessToken = params.headers.cookie.match(
        /accessToken=(.*?)(?![^;])/,
      )[1]
      commit('SET_MY_PROFILE', new Profile(data.userInfo, accessToken))
      commit(
        'SET_MY_WORKSPACES',
        data.workspaceInfoList.map(workspace => new Workspace(workspace)),
      )
    },
  },
}
