import AuthService from '../service/auth-service'

const login = JSON.parse(localStorage.getItem('login'))
const initialState = login
	? { status: { loggedIn: true }, user: login }
	: { status: {}, user: null }

export const auth = {
	namespaced: true,
	state: initialState,
	actions: {
		login({ commit }, login) {
			return AuthService.login(login).then(
				user => {
					commit('loginSuccess', user)
					return Promise.resolve(user)
				},
				error => {
					commit('loginFailure')
					return Promise.reject(error)
				},
			)
		},
		logout({ commit }) {
			AuthService.logout()
			commit('logout')
		},
		register({ commit }, user) {
			return AuthService.register(user).then(
				response => {
					commit('registerSuccess')
					return Promise.resolve(response.data)
				},
				error => {
					commit('registerFailure')
					return Promise.reject(error.response.data)
				},
			)
		},
	},
	mutations: {
		loginSuccess(state, user) {
			state.status = { loggedIn: true }
			state.user = user
		},
		loginFailure(state) {
			state.status = {}
			state.user = null
		},
		logout(state) {
			state.status = {}
			state.user = null
		},
		registerSuccess(state) {
			state.status = {}
		},
		registerFailure(state) {
			state.status = {}
		},
	},
}
