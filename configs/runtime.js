const env = process.env.VIRNECT_ENV
const configServer = process.env.CONFIG_SERVER

const axios = require('axios')

let envConfig = {}
let urlConfig = {}

const localUrls = {
	console: 'https://192.168.13.72:8888',
	api: 'https://192.168.6.3:8073',
	// api: 'http://192.168.13.34:8085',
	accout: 'https://localhost:8822',
	workstation: 'https://localhost:8878',
	download: 'https://localhost:8833',
	remote: 'https://localhost:8886',
	pay: 'https://192.168.13.72:7070',
	env: 'local',
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
			urlConfig.env = data.profiles[0]
		}
		return this
	}
}

const config = new Config()
module.exports = config
