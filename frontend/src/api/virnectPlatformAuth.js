import axios from 'axios'
import Cookies from 'js-cookie'
import clonedeep from 'lodash.clonedeep'

/**
 * 설정
 */
let API_GATEWAY_URL = {
	local: 'http://192.168.6.3:8073',
	develop: 'http://192.168.6.3:8073',
	staging: 'https://stgapi.virnect.com',
	production: 'https://api.virnect.com',
}[process.env.NODE_ENV]

let LOGIN_SITE_URL = {
	local: 'http://192.168.6.3:8883',
	develop: 'http://192.168.6.3:8883',
	staging: 'https://stgconsole.virnect.com',
	production: 'https://console.virnect.com',
}[process.env.NODE_ENV]

const api = axios.create({
	timeout: process.env.NODE_ENV === 'production' ? 2000 : 1000,
	headers: { 'Content-Type': 'application/json' },
})

/**
 * 상태
 */
let isLogin = false
let accessToken = null
let refreshToken = null
let myInfo = {}
let myWorkspaces = []

/**
 * 메소드
 */
function getTokensFromCookies() {
	accessToken = Cookies.get('accessToken')
	refreshToken = Cookies.get('refreshToken')
	return accessToken
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

	async init(options = {}) {
		if (options.API_GATEWAY_URL) API_GATEWAY_URL = options.API_GATEWAY_URL
		if (options.LOGIN_SITE_URL) LOGIN_SITE_URL = options.LOGIN_SITE_URL

		if (getTokensFromCookies()) {
			try {
				await this.getMyInfo()
				isLogin = true
			} catch (e) {
				console.error(e)
				console.log('Token is expired')
				isLogin = false
			}
		}
		return this
	}
	async getMyInfo() {
		api.defaults.headers.common = {
			Authorization: `Bearer ${accessToken}`,
		}
		const res = await api(`${API_GATEWAY_URL}/users/info`)
		const { data } = res.data
		myInfo = data.userInfo
		myWorkspaces = data.workspaceInfoList.workspaceList
		return data
	}
	login() {
		location.href = `${LOGIN_SITE_URL}/?continue=${location.href}`
		return this
	}
	logout() {
		Cookies.remove('accessToken')
		Cookies.remove('refreshToken')
		isLogin = false
		accessToken = null
		refreshToken = null
		myInfo = {}
		myWorkspaces = []
		return this
	}
}

const auth = new Auth()
export default auth
