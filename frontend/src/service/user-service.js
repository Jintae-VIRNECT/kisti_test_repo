import Axios from 'axios'
import authHeader from './auth-header'
import API from './url'

const GATEWAY_API_URL = 'http://192.168.6.3:8081'

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

	// signup(user) {
	// 	return axios.post(GATEWAY_API_URL + API.auth.signup, {
	// 		username: user.username,
	// 		email: user.email,
	// 		password: user.password,
	// 	})
	// }

	async signup(user = {}) {
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
				sessionCode: user.session,
			})
			console.log(user)
			console.log('++++++++++++++')
			console.log(response.data)
			return response.data
		} catch (e) {
			console.error(e)
		}
	}
}

export default new UserService()
