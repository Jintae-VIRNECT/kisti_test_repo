import { api } from 'api/axios'

export default {
	async login(params) {
		const data = await api('POST_SIGHIN', params)
		return data
	},

	async signUp(params) {
		const data = await api('POST_SIGHUP', params)
		return data
	},

	async emailAuth(params) {
		const response = await api('POST_EMAIL_AUTH', {
			params,
		})
		return response
	},

	async verification(params) {
		const response = await api('GET_VERIFICATION', params)
		return response
	},

	async qrOtp(params) {
		const response = await api('POST_OTP_QR', params)
		return response
	},
}
