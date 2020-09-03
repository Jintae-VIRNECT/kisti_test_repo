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
		userDetail: {},
		verification: {},
	},
	getters: {
		signup(state) {
			return state.signup
		},
		userDetail(state) {
			return state.userDetail
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
		async register(context, param) {
			const res = await UserService.register(param)
			context.commit('signupResponse', res.data)
			if (res.code === 200) {
				return res.data
			} else {
				throw new Error(`${res.code}: ${res.message}`)
			}
		},
		async userDetail(context, param) {
			const res = await UserService.userDetail(param)
			context.commit('userDetailResponse', res.data)
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
		signupResponse(state, result) {
			state.signup = result
		},
		userDetailResponse(state, result) {
			state.userDetail = result
		},
		verificationResponse(state, result) {
			state.verification = result
		},
	},
}
