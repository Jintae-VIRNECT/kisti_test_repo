import AuthService from '../service/auth-service'
import UserService from '../service/user-service'

const login = JSON.parse(localStorage.getItem('user'))
const initialState = login
	? { status: { loggedIn: true }, user: login }
	: { status: {}, user: null }

export const auth = {
	namespaced: true,
	state: {
		initial: initialState,
		signup: {},
		verification: {},
	},
	getters: {
		signup(state) {
			return state.signup
		},
		verification(state) {
			return state.verification
		},
	},
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
		// signup({ commit }, user) {
		// 	return AuthService.signup(user).then(
		// 		response => {
		// 			commit('registerSuccess')
		// 			return Promise.resolve(response.data)
		// 		},
		// 		error => {
		// 			commit('registerFailure')
		// 			return Promise.reject(error.response.data)
		// 		},
		// 	)
		// },
		async signup(context, param) {
			const res = await UserService.signup(param)
			context.commit('signupResponse', res.data)
			if (res.code === 200) {
				return res.data
			} else {
				throw new Error(`${res.code}: ${res.message}`)
			}
		},
		async verification(context, param) {
			const res = await AuthService.verification(param)
			context.commit('verificationResponse', res.data)
			if (res.code === 200) {
				return res.data
			} else {
				throw new Error(`${res.code}: ${res.message}`)
			}
		},
	},
	mutations: {
		loginSuccess(state, user) {
			state.initial.status = { loggedIn: true }
			state.initial.user = user
		},
		loginFailure(state) {
			state.initial.status = {}
			state.initial.user = null
		},
		logout(state) {
			state.initial.status = {}
			state.initial.user = null
		},
		registerSuccess(state) {
			state.initial.status = {}
		},
		registerFailure(state) {
			state.initial.status = {}
		},
		signupResponse(state, result) {
			state.verification = result
		},
		verificationResponse(state, result) {
			state.verification = result
		},
	},
}
