const users = [
	{
		email: 'antmgu@gmail.com',
		password: '12341234',
	},
]

export default {
	state: {
		isLoggedIn: false,
		me: {
			uid: null,
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
		USER_LOGIN(state, { email, role, uid }) {
			state.me.uid = uid
			state.me.email = email
			state.me.role = role
			state.isLoggedIn = true
		},
		USER_SET_LOCALE(state, { locale }) {
			state.locale = locale
		},
		USER_LOGOUT(state) {
			state.me.uid = null
			state.me.email = null
			state.me.role = null
			state.isLoggedIn = false
		},
	},
	actions: {
		USER_LOGIN(context, { user }) {
			return new Promise((resolve, reject) => {
				const checkUser = users.find(
					u => u.email == user.email && u.password === user.password,
				)
				if (checkUser) {
					context.commit('USER_LOGIN', checkUser)
					resolve(checkUser)
				} else reject(new Error('User not exist'))
			})
		},
		USER_LOGOUT(context) {
			return new Promise(resolve => {
				context.commit('USER_LOGOUT')
				resolve()
			})
		},
	},
}
