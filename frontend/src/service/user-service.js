import Axios from 'axios'
import authHeader from './auth-header'
import API from './url'

const GATEWAY_API_URL = 'https://192.168.6.3:8073'

const axios = Axios.create({
	timeout: 10000,
	headers: {
		'Content-Type': 'application/json',
	},
})

class UserService {
	getUserContent() {
		return axios.get(GATEWAY_API_URL + API.user.userInfo, {
			headers: authHeader(),
		})
	}

	async register(user = {}) {
		try {
			const response = await axios.post(GATEWAY_API_URL + API.user.register, {
				email: user.email,
				password: user.password,
				firstName: user.firstName,
				lastName: user.lastName,
				birth: user.birth,
				marketInfoReceive: user.marketInfoReceive,
				joinInfo: user.joinInfo,
				serviceInfo: user.serviceInfo,
				sessionCode: user.sessionCode,
			})
			return response.data
		} catch (e) {
			console.error(e)
		}
	}

	async userDetail(user = {}) {
		try {
			const response = await axios.post(
				GATEWAY_API_URL + API.user.registerDetail,
				user,
				{
					headers: {
						'Content-Type': 'multipart/form-data',
					},
				},
			)
			// console.log(user)
			// console.log('++++++++++++++')
			// console.log(response.data)
			return response.data
		} catch (e) {
			console.error(e)
		}
	}
}

export default new UserService()
