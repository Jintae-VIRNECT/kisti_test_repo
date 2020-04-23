import axios from 'axios'
import Cookies from 'js-cookie'
import clonedeep from 'lodash.clonedeep'
import urls from './urls'

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

/**
 * 메소드
 */
function getTokensFromCookies() {
	accessToken = Cookies.get('accessToken')
	refreshToken = Cookies.get('refreshToken')
	return accessToken
}
async function getMyInfo() {
	api.defaults.headers.common = {
		Authorization: `Bearer ${accessToken}`,
	}
	const res = await api('/users/info')
	const { data } = res.data
	myInfo = data.userInfo
	myWorkspaces = data.workspaceInfoList.workspaceList
	return data
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
		const env = process.env.TARGET_ENV
		api = axios.create({
			timeout: process.env.TARGET_ENV === 'production' ? 2000 : 1000,
			headers: { 'Content-Type': 'application/json' },
			baseURL: options.API_GATEWAY_URL
				? options.API_GATEWAY_URL
				: urls.api[env],
		})

		if (getTokensFromCookies()) {
			try {
				await getMyInfo()
				isLogin = true
			} catch (e) {
				console.error(e)
				console.error('Token is expired')
				isLogin = false
			}
		}
		return this
	}
	login() {
		const url = options.LOGIN_SITE_URL
			? options.LOGIN_SITE_URL
			: urls.console[env]
		location.href = `${url}/?continue=${location.href}`
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
