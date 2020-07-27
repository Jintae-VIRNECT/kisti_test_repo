import axios from '../api/axios'
import authHeader from './auth-header'
import API from '../api/url'

class UserService {
	getUserContent() {
		return axios.get(API.user.userInfo, {
			headers: authHeader(),
		})
	}

	//유저 상세정보
	async userDetail(user = {}) {
		try {
			const response = await axios.post(API.user.registerDetail, user, {
				headers: {
					'Content-Type': 'multipart/form-data',
				},
			})
			return response.data
		} catch (e) {
			console.error(e)
		}
	}

	// 이메일로 찾기
	async userFindEmail(user = {}) {
		try {
			const response = await axios.post(API.user.findEmail, {
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
			const response = await axios.post(API.user.findPass, {
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
			const response = await axios.post(API.user.passCodeCheck, {
				code: user.code,
				email: user.email,
			})
			return response.data
		} catch (e) {
			console.error(e)
		}
	}

	//비밀번호 재설정 - 비번 재설정
	async userPassChange(user = {}) {
		try {
			const response = await axios.put(API.user.changePass, {
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
