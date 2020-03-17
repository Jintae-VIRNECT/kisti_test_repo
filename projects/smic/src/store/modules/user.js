import api from '@/api/gateway'

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
    allMembersList: [],
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
    allMembersList(state) {
      return state.allMembersList
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
      state.me.qrCode = user.qrCode
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
      state.me.qrCode = null
      state.isLoggedIn = false
      state.workspaceInfoList = []
    },
    SET_ALL_MEMBERS_LIST(state, list) {
      state.allMembersList = list
    },
  },
  actions: {
    async USER_LOGIN(context, { email, password }) {
      const data = await api('USER_LOGIN', {
        params: { email, password },
      })

      if (Object.keys(data).length === 0) return { isLogin: false }

      context.commit('USER_LOGIN', {
        user: data.userInfo,
        workspaceInfoList: data.workspaceInfoList,
      })
      return data
    },
    async USER_LOGOUT(context) {
      const data = await api('USER_LOGOUT')
      context.commit('USER_LOGOUT')
      return data
    },
    async getAllMembersList(context) {
      const data = await api('MEMBER_LIST', {
        params: {
          userId: this.getters.getUser.uuid,
          size: 100,
          sort: 'name,asc',
        },
      })
      context.commit('SET_ALL_MEMBERS_LIST', data.memberInfoList)
      return data
    },
  },
}
