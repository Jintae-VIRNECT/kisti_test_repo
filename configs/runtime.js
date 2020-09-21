const fs = require('fs')
const dotenv = require('dotenv')
const filePath = `.env.${process.env.VIRNECT_ENV.trim()}`
const parseConfig = dotenv.parse(fs.readFileSync(filePath))
const urlsConfig = JSON.parse(fs.readFileSync('./configs/urls.json'))

const envs = process.env.VIRNECT_ENV.trim()
const configServer =
	process.env.CONFIG_SERVER == undefined
		? parseConfig.CONFIG_SERVER
		: process.env.CONFIG_SERVER.trim()

const axios = require('axios')

let envConfig = {}
let urlConfig = {}

module.exports = {
	async init() {
		const { data } = await axios.get(`${configServer}/login-web/${envs}`)
		const property = data.propertySources[0].source
		for (let key in property) {
			if (key.includes('url.')) {
				key.slice(4)
				urlConfig[key.slice(4)] = property[key]
			}
			if (key.includes('env.')) {
				key.slice(4)
				envConfig[key.slice(4)] = property[key]
			}
		}
		// console.log(property)
		urlConfig.env = property['env.VIRNECT_ENV']
		return {
			envConfig: envConfig,
			urlConfig: urlConfig,
		}
	},
	getTargetEnv() {
		return String(parseConfig['VIRNECT_ENV'])
	},
	getDevUrls() {
		const urls = {}
		const env = this.getTargetEnv()
		Object.keys(urlsConfig).forEach(key => {
			urls[key] = urlsConfig[key][env]
		})
		urls.env = env
		return urls
	},
}
