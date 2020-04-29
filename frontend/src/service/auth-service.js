import Cookies from 'js-cookie'
import Axios from 'axios'
import API from './url'

const GATEWAY_API_URL = process.env.API_GATEWAY_URL

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
				rememberMe: user.rememberMe,
			})
			.then(this.handleResponse)
			.then(response => {
				const { data } = response
				if (data.accessToken) {
					const cookieOption = {
						expires: data.expireIn / 3600000,
						domain:
							location.hostname.split('.').length === 3
								? location.hostname.replace(/.*?\./, '')
								: location.hostname,
					}
					Cookies.set('accessToken', data.accessToken, cookieOption)
					Cookies.set('refreshToken', data.refreshToken, cookieOption)
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
				console.log(data)
				if (data !== undefined) {
					localStorage.removeItem('user')
					return data
				}
				return data
			})
	}

	//가입정보
	async signUp(user = {}) {
		try {
			const response = await axios.post(
				GATEWAY_API_URL + API.auth.signup,
				user,
				{
					headers: {
						'Content-Type': 'multipart/form-data',
					},
				},
			)
			return response.data
		} catch (e) {
			console.error(e)
		}
	}

	async emailAuth(email = {}) {
		try {
			const response = await axios.post(GATEWAY_API_URL + API.auth.emailAuth, {
				email,
			})
			return response.data
		} catch (e) {
			console.error(e)
		}
	}

	async verification(code = {}) {
		try {
			const response = await axios.get(
				GATEWAY_API_URL + API.auth.verification,
				{
					params: {
						code: code.code,
						email: code.email,
					},
				},
			)
			return response.data
		} catch (e) {
			console.error(e)
		}
	}

	async qrOtp(code = {}) {
		try {
			const response = await axios.post(GATEWAY_API_URL + API.auth.qrOtp, {
				email: code.email,
				userId: code.userId,
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
