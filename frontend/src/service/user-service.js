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

	//가입정보
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

	//유저 상세정보
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
			return response.data
		} catch (e) {
			console.error(e)
		}
	}

	// 이메일로 찾기
	async userFindEmail(user = {}) {
		try {
			const response = await axios.post(GATEWAY_API_URL + API.user.findEmail, {
				firstName: user.firstName,
				lastName: user.lastName,
				mobile: user.mobile,
				recoveryEmail: user.recoveryEmail,
			})
			return response.data
		} catch (e) {
			console.error(e)
		}
	}

	//비밀번호 재설정 - 이메일 코드 발송
	async userPass(user = {}) {
		try {
			const response = await axios.post(GATEWAY_API_URL + API.user.findPass, {
				email: user.email,
			})
			return response.data
		} catch (e) {
			console.error(e)
		}
	}

	//비밀번호 재설정 - 코드 체크
	async userCodeCheck(user = {}) {
		try {
			const response = await axios.post(
				GATEWAY_API_URL + API.user.passCodeCheck,
				{
					code: user.code,
					email: user.email,
				},
			)
			return response.data
		} catch (e) {
			console.error(e)
		}
	}

	//비밀번호 재설정 - 비번 재설정
	async userPassChange(user = {}) {
		try {
			const response = await axios.put(GATEWAY_API_URL + API.user.changePass, {
				uuid: user.uuid,
				email: user.email,
				password: user.password,
			})
			return response.data
		} catch (e) {
			console.error(e)
		}
	}
}

export default new UserService()
