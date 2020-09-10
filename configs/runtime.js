const fs = require('fs')
const dotenv = require('dotenv')
const urlsConfig = JSON.parse(fs.readFileSync('./configs/urls.json'))
const filePath = `.env.${process.env.NODE_ENV.trim()}`
const envConfig = dotenv.parse(fs.readFileSync(filePath))

module.exports = {
	getTargetEnv() {
		return String(envConfig['TARGET_ENV'])
	},
	getUrls() {
		const urls = {}
		const env = this.getTargetEnv()
		Object.keys(urlsConfig).forEach(key => {
			urls[key] = urlsConfig[key][env]
		})
		return urls
	},
	getEnvUrls(env) {
		const urls = {}
		Object.keys(urlsConfig).forEach(key => {
			urls[key] = urlsConfig[key][env]
		})
		return urls
	},
}
