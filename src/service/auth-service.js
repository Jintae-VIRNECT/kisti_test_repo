import Cookies from 'js-cookie'
import axios from '../api/axios'
import API from '../api/url'
import Auth from 'WC-Modules/javascript/api/virnectPlatform/virnectPlatformAuth'

class AuthService {
	login(user) {
		return axios
			.post(API.auth.login, {
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
			.post(API.auth.logout, {
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
			const response = await axios.post(API.auth.signup, user, {
				headers: {
					'Content-Type': 'multipart/form-data',
					Authorization: 'Bearer ' + Auth.accessToken,
				},
			})
			return response.data
		} catch (e) {
			console.error(e)
		}
	}

	async emailAuth(email = {}) {
		try {
			const response = await axios.post(API.auth.emailAuth, {
				email,
			})
			return response.data
		} catch (e) {
			console.error(e)
		}
	}

	async verification(code = {}) {
		try {
			const response = await axios.get(API.auth.verification, {
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

	async qrOtp(code = {}) {
		try {
			const response = await axios.post(API.auth.qrOtp, {
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
			return Promise.reject(data)
		}
		return Promise.resolve(data)
	}
}

export default new AuthService()
