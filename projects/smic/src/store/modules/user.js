import Vue from 'vue'

export default {
  state: {
    isLoggedIn: false,
    me: {
      uuid: null,
      name: null,
      email: null,
      description: null,
      userType: null,
      profile: null,
      loginLock: null,
    },
    workspaceInfoList: [
      // {
      //   uuid
      //   pinNumber
      //   description
      //   role
      // }...
    ],
    locale: null,
    lastAccessPath: null,
  },
  getters: {
    getLocale(state) {
      return state.locale
    },
    getUser(state) {
      return state.me
    },
    getIsLoggedIn(state) {
      return state.isLoggedIn
    },
    getLastAccessPath(state) {
      return state.lastAccessPath
    },
    getWorkspaceName(state) {
      // 레거시 코드 - 워크스페이스가 여러개가 되면 가져오는 방식을 바꿔야함.
      if (!state.workspaceInfoList[0]) return null
      return state.workspaceInfoList[0].description
    },
  },
  mutations: {
    USER_SET_LAST_ACCESS_PATH(state, { path }) {
      state.lastAccessPath = path
    },
    USER_LOGIN(state, { user, workspaceInfoList }) {
      state.me.uuid = user.uuid
      state.me.name = user.name
      state.me.email = user.email
      state.me.description = user.description
      state.me.userType = user.userType
      state.me.profile = user.profile
      state.me.loginLock = user.loginLock
      state.isLoggedIn = true
      state.workspaceInfoList = workspaceInfoList
    },
    USER_SET_LOCALE(state, { locale }) {
      state.locale = locale
    },
    USER_LOGOUT(state) {
      state.me.uuid = null
      state.me.name = null
      state.me.email = null
      state.me.description = null
      state.me.userType = null
      state.me.profile = null
      state.me.loginLock = null
      state.isLoggedIn = false
      state.workspaceInfoList = []
    },
  },
  actions: {
    async USER_LOGIN(context, { email, password }) {
      try {
        const response = await Vue.axios.post('/users/login', {
          email,
          password,
        })
        const { data } = response.data
        context.commit('USER_LOGIN', {
          user: data.userInfo,
          workspaceInfoList: data.workspaceInfoList,
        })
        return response.data
      } catch (e) {
        console.log('e : ', e)
      }
    },
    async USER_LOGOUT(context) {
      try {
        const response = await Vue.axios.get('/users/logout')
        context.commit('USER_LOGOUT')
        return response.data
      } catch (e) {
        console.log('e : ', e)
      }
    },
  },
}
