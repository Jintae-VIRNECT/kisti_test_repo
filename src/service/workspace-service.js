import { api } from 'api/axios'

export default {
	async customConfig(params) {
		// 아이디 확인
		try {
			const data = await api('GET_CUSTOM_CONFIG', {
				route: params,
				params: params,
			})
			return data
		} catch (e) {
			return e
		}
	},
}
