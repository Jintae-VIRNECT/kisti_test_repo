import Vue from 'vue'

export default {
  state: {
    isLoggedIn: false,
    me: {
      uid: null,
      username: null,
      id: null,
      email: null,
      role: null,
    },
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
    getAuth(state) {
      return state.me.role
    },
    getLastAccessPath(state) {
      return state.lastAccessPath
    },
  },
  mutations: {
    USER_SET_LAST_ACCESS_PATH(state, { path }) {
      state.lastAccessPath = path
    },
    USER_LOGIN(state, { id, role, uid, username, email }) {
      state.me.uid = uid
      state.me.id = id
      state.me.username = username
      state.me.email = email
      state.me.role = role
      state.isLoggedIn = true
    },
    USER_SET_LOCALE(state, { locale }) {
      state.locale = locale
    },
    USER_LOGOUT(state) {
      state.me.uid = null
      state.me.id = null
      state.me.username = null
      state.me.role = null
      state.isLoggedIn = false
    },
  },
  actions: {
    USER_LOGIN(context, { email, password }) {
      return new Promise((resolve, reject) => {
        Vue.axios
          .post('/users/login', { params: { email, password } })
          .then(res => {
            context.commit('USER_LOGIN', { email })
            resolve(res)
          })
          .catch(e => {
            // console.log('e : ', e)
            reject(e)
          })
      })
      // return new Promise((resolve, reject) => {
      //   const checkUser = users.find(
      //     u => u.id == user.id && u.password === user.password,
      //   )
      //   if (checkUser) {
      //     context.commit('USER_LOGIN', checkUser)
      //     resolve(checkUser)
      //   } else return reject(false)
      // })
    },
    USER_LOGOUT(context) {
      // return new Promise(resolve => {
      //   context.commit('USER_LOGOUT')
      //   resolve()
      // })
      Vue.axios
        .get('/users/login')
        .then(res => {
          console.log('res : ', res)
          context.commit('USER_LOGOUT')
        })
        .catch(e => console.log('e : ', e))
    },
  },
}
