import Axios from 'axios'
import https from 'https'
import Cookies from 'js-cookie'
import URI from 'api/uri'
const env = process.env.NODE_ENV
const isProduction = /production|staging/.test(process.env.NODE_ENV)

export async function getUrls() {
	const res = await Axios.get(
		`${location.origin}/urls?origin=${location.hostname}`,
	)
	window.urls = res.data
	window.env = res.data.env
	setBaseURL(res.data)
	return res.data
}

const axios = Axios.create({
	headers: {
		'Content-Type': 'application/json',
	},
	httpsAgent: new https.Agent({
		rejectUnauthorized: false,
	}),
	withCredentials: isProduction,
})

const setBaseURL = baseURL => {
	axios.defaults.timeout = baseURL.timeout
	axios.defaults['baseURL'] = baseURL.api
	axios.defaults.headers['Access-Control-Allow-Origin'] = baseURL.api
}

/**
 * Api gateway
 * @param {String} name
 * @param {Object} option
 */
export async function api(name, option = {}) {
	if (!isProduction) {
		console.log(axios.defaults)
	}
	if (!URI[name]) {
		throw new Error(`API not found '${name}'`)
	}
	let [method, uri] = URI[name]
	let { route, params, headers } = option

	// replace route
	method = method.toLowerCase()
	uri = !route
		? uri
		: Object.entries(route).reduce((u, q) => {
				return u.replace(`{${q[0]}}`, q[1])
		  }, uri)

	// filter ALL -> null
	if (params && params.filter && params.filter === 'ALL') {
		delete params.filter
	}

	// GET, DELETE
	if (method === 'get') params = { params }
	if (method === 'delete') params = { data: params }

	// default header
	const accessToken = Cookies.get('accessToken')
		? Cookies.get('accessToken')
		: headers && headers.cookie.match(/accessToken=(.*?)(?![^;])/)[1]
	if (accessToken) {
		axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`
	}
	try {
		const response = await axios[method](uri, params, headers)
		const { code, data, message } = response.data

		if (code === 200) {
			if (/local|develop|onpremise/.test(env)) {
				console.log(data)
			}
			return {
				code: code,
				data: data,
			}
		} else if (code === 8003 || code === 8005) {
			if (process.client) location.href = window.urls.console
			throw new Error(`${code}: ${message}`)
		} else {
			const error = new Error(`${code}: ${message}`)
			console.error(error)
			return {
				code: code,
				message: message,
			}
		}
	} catch (e) {
		console.error(`URL: ${uri}`)
		if (/local|develop|onpremise/.test(env)) {
			console.error(e)
		}
		throw e
	}
}

module.exports = { api, getUrls, setBaseURL }
