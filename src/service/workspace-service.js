import { api } from 'api/axios'

export default {
	async customConfig() {
		// 아이디 확인
		try {
			const data = await api('GET_CUSTOM_CONFIG')
			return data
		} catch (e) {
			return e
		}
	},
}
