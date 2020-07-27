import Axios from 'axios'
import urls from 'WC-Modules/javascript/api/virnectPlatform/urls'

const axios = Axios.create({
	baseURL: urls.api[process.env.TARGET_ENV],
	timeout: process.env.API_TIMEOUT,
	headers: {
		'Content-Type': 'application/json',
	},
})
axios.interceptors.response.use(
	res => {
		return res
	},
	err => {
		if (err.code === 'ECONNABORTED') {
			location.href = '/504'
		}
		console.error(err)
		throw err
	},
)

export default axios
