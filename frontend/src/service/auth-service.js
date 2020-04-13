import Axios from 'axios'
import API from './url'

const GATEWAY_API_URL = 'http://192.168.6.3:8073'
const AUTH_API_URL = 'http://192.168.6.3:8321'

const axios = Axios.create({
	timeout: 10000,
	headers: {
		'Content-Type': 'application/json',
	},
})

class AuthService {
	login(user) {
		return axios
			.post(AUTH_API_URL + API.auth.login, {
				email: user.email,
				password: user.password,
				rememberMe: user.rememberMe,
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
			.post(AUTH_API_URL + API.auth.logout, {
				uuid: logout.uuid,
				accessToken: logout.accessToken,
			})
			.then(this.handleResponse)
			.then(response => {
				const { data } = response
				// alert(data);
				console.log(data)
				if (data !== undefined) {
					localStorage.removeItem('user')
					return data
				}
				return data
			})
	}

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
			// alert(data)
			// console.log(Promise.reject(error))
			return Promise.reject(error)
		}
		return Promise.resolve(data)
	}
}

export default new AuthService()
