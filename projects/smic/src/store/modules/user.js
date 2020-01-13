import users from '@/data/users'
import Vue from 'vue'

export default {
	state: {
		isLoggedIn: false,
		me: {
			uid: null,
			username: null,
			id: null,
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
		USER_LOGIN(state, { id, role, uid, username }) {
			state.me.uid = uid
			state.me.id = id
			state.me.username = username
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
		USER_LOGIN(context, { user }) {
			return new Promise((resolve, reject) => {
				const checkUser = users.find(
					u => u.id == user.id && u.password === user.password,
				)
				if (checkUser) {
					context.commit('USER_LOGIN', checkUser)
					resolve(checkUser)
				} else
					reject(
						Vue.swal.fire({
							type: 'error',
							title: '올바른 정보가 아닙니다. 확인 후 재시도 해주세요.',
							toast: true,
							position: 'center-center',
							showConfirmButton: false,
							timer: 3000,
						}),
					)
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
