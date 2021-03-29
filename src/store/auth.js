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
