const env = process.env.VIRNECT_ENV
const configServer = process.env.CONFIG_SERVER
	? process.env.CONFIG_SERVER
	: 'http://192.168.6.3:6383'

const axios = require('axios')

let envConfig = {}
let urlConfig = {}

const localIp = '192.168.13.72'

const localUrls = {
	www: `https://${localIp}:9010`,
	console: `https://${localIp}8888`,
	api: 'https://192.168.6.3:8073',
	accout: 'https://localhost:8822',
	workstation: 'https://localhost:8878',
	download: 'https://localhost:8833',
	remote: 'https://localhost:8886',
	pay: `https://${localIp}:7070`,
	env: 'local',
	timeout: 30000,
}

class Config {
	constructor() {}

	get envConfig() {
		return envConfig
	}
	get urlConfig() {
		return urlConfig
	}

	async init() {
		if (env === 'local') {
			urlConfig = localUrls
			envConfig = localUrls.env
		} else {
			const { data } = await axios.get(
				`${configServer}/web-url/${env === 'local' ? 'develop' : env}`,
			)
			const res = await axios.get(
				`${configServer}/login-web/${env === 'local' ? 'develop' : env}`,
			)
			urlConfig = data.propertySources[0].source
			envConfig = res.data.propertySources[0].source
			urlConfig.timeout = res.data.propertySources[0].source.API_TIMEOUT
			urlConfig.env = data.profiles[0]
		}
		return this
	}
}

const config = new Config()
module.exports = config
