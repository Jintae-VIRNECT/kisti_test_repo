import Axios from 'axios'
import API from './url'

const GATEWAY_API_URL = 'http://192.168.6.3:8073'
const AUTH_API_URL = 'http://192.168.6.3:8321'
const USER_API_URL = 'http://192.168.6.3:8081'

const axios = Axios.create({
	timeout: 10000,
	headers: {
		'Content-Type': 'application/json',
	},
})

class AuthService {
	login(user) {
		return axios
			.post(GATEWAY_API_URL + API.auth.login, {
				email: user.email,
				password: user.password,
			})
			.then(this.handleResponse)
			.then(response => {
				const { data } = response
				console.log(data)
				if (data.accessToken) {
					localStorage.setItem('user', JSON.stringify(data))
				}
				return data
			})
	}

	logout(logout) {
		return axios
			.post(GATEWAY_API_URL + API.auth.logout, {
				uuid: logout.uuid,
				accessToken: logout.accessToken,
			})
			.then(this.handleResponse)
			.then(response => {
				const { data } = response
				// alert(data);
				if (data !== undefined) {
					localStorage.removeItem('user')
					return data
				}
				return data
			})
	}

	// signup(user) {
	// 	return axios.post(GATEWAY_API_URL + API.auth.signup, {
	// 		username: user.username,
	// 		email: user.email,
	// 		password: user.password,
	// 	})
	// }

	emailAuth(email) {
		return axios
			.post(AUTH_API_URL + API.auth.emailAuth, {
				email,
			})
			.then(this.handleResponse)
			.then(response => {
				const { data } = response.data
				return data
			})
	}

	async signup(user = {}) {
		try {
			const response = await axios.post(USER_API_URL + API.user.register, {
				birth: user.birth,
				description: '',
				email: user.email,
				joinInfo: user.joinInfo,
				firstName: user.firstName,
				lastName: user.lastName,
				mobile: '',
				marketInfoReceive: user.marketInfoReceive,
				password: user.password,
				profile: '',
				recoveryEmail: '',
				serviceInfo: user.serviceInfo,
				sessionCode: user.session,
			})
			console.log(response.data)
			return response.data
		} catch (e) {
			console.error(e)
		}
	}

	async verification(code = {}) {
		try {
			const response = await axios.get(AUTH_API_URL + API.auth.verification, {
				params: {
					code: code.code,
					email: code.email,
				},
			})
			return response.data
		} catch (e) {
			console.error(e)
		}
	}

	handleResponse(response) {
		const { data } = response
		if (response.status !== 200 || data.code !== 200) {
			// console.log(data.code)
			// console.log(response.status)
			localStorage.removeItem('user')
			// location.reload(true);

			const error = data.message
			alert(data)
			// console.log(Promise.reject(error))
			return Promise.reject(error)
		}
		return Promise.resolve(data)
	}
}

export default new AuthService()
