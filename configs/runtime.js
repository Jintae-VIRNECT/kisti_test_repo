const env = process.env.VIRNECT_ENV
const configServer = process.env.CONFIG_SERVER

const axios = require('axios')

let envConfig = {}
let urlConfig = {}

class Config {
	constructor() {}

	get envConfig() {
		return envConfig
	}
	get urlConfig() {
		return urlConfig
	}

	async init() {
		const { data } = await axios.get(
			`${configServer}/web-url/${env === 'local' ? 'develop' : env}`,
		)
		const res = await axios.get(
			`${configServer}/login-web/${env === 'local' ? 'develop' : env}`,
		)
		urlConfig = data.propertySources[0].source
		envConfig = res.data.propertySources[0].source
		urlConfig.env = data.profiles[0]
		return this
	}
}

const config = new Config()
module.exports = config
