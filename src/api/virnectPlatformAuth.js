import axios from 'axios'
import Cookies from 'js-cookie'
import clonedeep from 'lodash.clonedeep'
import jwtDecode from 'jwt-decode'
import runtime from 'api/axios'

/**
 * 상태
 */
let env = null
let api = null
let isLogin = false
let accessToken = null
let refreshToken = null
let myInfo = {}
let myWorkspaces = []
let initOptions = {}
const intervalTime = 5 * 60 * 1000 // 5 minutes
const renewvalTime = 5 * 60 // 5 minutes
let interval

/**
 * 메소드
 */

function getTokensFromCookies() {
	accessToken = Cookies.get('accessToken')
	refreshToken = Cookies.get('refreshToken')
	return accessToken
}
function setTokensToCookies(data) {
	const cookieOption = {
		// secure: true,
		// sameSite: 'None',
		expires: data.expireIn / 3600000,
		domain:
			location.hostname.split('.').length === 3
				? location.hostname.replace(/.*?\./, '')
				: location.hostname,
	}
	Cookies.set('accessToken', data.accessToken, cookieOption)
	Cookies.set('refreshToken', data.refreshToken, cookieOption)
	accessToken = data.accessToken
	refreshToken = data.refreshToken
	api.defaults.headers.common = {
		Authorization: `Bearer ${accessToken}`,
	}
}
const tokenInterval = async () => {
	const expireTime = jwtDecode(accessToken).exp,
		now = parseInt(Date.now() / 1000)

	// check expire time
	if (expireTime - now < renewvalTime) {
		const params = {
			accessToken: accessToken,
			refreshToken: refreshToken,
		}

		try {
			const res = await api.post('/auth/oauth/token', params)
			const { data } = res.data
			setTokensToCookies(data)
		} catch (e) {
			auth.logout()
		}
	}
}
const tokenRenewal = () => {
	if (interval) {
		clearInterval(interval)
	}
	interval = setInterval(tokenInterval, intervalTime)
	tokenInterval()
}
async function getMyInfo() {
	api.defaults.headers.common = {
		Authorization: `Bearer ${accessToken}`,
	}
	const res = await api('/users/info')
	const { code, message } = res.data
	if (code !== 200) throw new Error(`${code}: ${message}`)

	const { data } = res.data
	myInfo = data.userInfo
	myWorkspaces = data.workspaceInfoList.workspaceList
	return data
}

async function getUrls() {
	const res = await runtime.getUrls()
	return res
}

/**
 * export
 */
class Auth {
	constructor() {}

	get isLogin() {
		return isLogin
	}
	get accessToken() {
		return accessToken
	}
	get refreshToken() {
		return refreshToken
	}
	get myInfo() {
		return clonedeep(myInfo)
	}
	get myWorkspaces() {
		return clonedeep(myWorkspaces)
	}
	get env() {
		return env
	}

	async init(options = {}) {
		let urls = await getUrls()
		// console.log(urls.api)
		initOptions = options
		api = axios.create({
			timeout: process.env.TARGET_ENV === 'production' ? 2000 : 1000,
			headers: { 'Content-Type': 'application/json' },
			baseURL: urls.api,
		})

		if (getTokensFromCookies()) {
			try {
				await getMyInfo()
				isLogin = true
				tokenRenewal()
			} catch (e) {
				console.error(e)
				isLogin = false
			}
		}
		return this
	}
	login() {
		const url = initOptions.LOGIN_SITE_URL
			? initOptions.LOGIN_SITE_URL
			: urls.console
		location.href = `${url}/?continue=${location.href}`
		return this
	}
}

const auth = new Auth()
export default auth
