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

	// onpremise //
	async userAuth(params) {
		// 아이디 확인
		try {
			const { data } = await api('POST_CHECK_UUID', {
				params: params,
			})
			return data
		} catch (e) {
			return e
		}
	},
	async userCheckAnswer(params) {
		// 질문확인
		try {
			const data = await api('POST_CHECK_ANSWER', {
				params: params,
			})
			return data
		} catch (e) {
			return e
		}
	},
	async putUserPassChange(params) {
		//비번변경
		try {
			const data = await api('PUT_USER_PASSWORD', {
				params: params,
			})
			return data
		} catch (e) {
			return e
		}
	},
}
