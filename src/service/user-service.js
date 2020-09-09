import { api } from 'api/axios'

export default {
	//유저 상세정보
	async userDetail(params) {
		try {
			const response = await api('GET_USER_INFO', params, {
				headers: {
					'Content-Type': 'multipart/form-data',
				},
			})
			return response
		} catch (e) {
			console.error(e)
		}
	},

	async userFindEmail(params) {
		// 이메일로 찾기
		try {
			const response = await api('POST_FIND_EMAIL', params)
			return response
		} catch (e) {
			console.error(e)
		}
	},

	//비밀번호 재설정 - 이메일 코드 발송
	async userPass(params) {
		try {
			const response = await api('POST_FIND_PASS', params)
			return response
		} catch (e) {
			console.error(e)
		}
	},

	//비밀번호 재설정 - 코드 체크
	async userCodeCheck(params) {
		try {
			const response = await api('POST_CODE_CHECK', params)
			return response
		} catch (e) {
			console.error(e)
		}
	},

	//비밀번호 재설정 - 비번 재설정
	async userPassChange(params) {
		try {
			const response = await api('PUT_PASS', params)
			return response
		} catch (e) {
			console.error(e)
		}
	},
}
