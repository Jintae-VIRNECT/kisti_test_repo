import axios from 'axios'
import Cookies from 'js-cookie'

/**
 * 설정
 */
const API_GATEWAY_URL = {
  local: 'http://192.168.6.3:8073',
  develop: 'http://192.168.6.3:8073',
  staging: 'https://stgapi.virnect.com',
  production: 'https://api.virnect.com',
}[process.env.NODE_ENV]

const LOGIN_SITE_URL = {
  local: 'http://192.168.6.3:8883',
  develop: 'http://192.168.6.3:8883',
  staging: 'https://stgconsole.virnect.com',
  production: 'https://console.virnect.com',
}[process.env.NODE_ENV]

const api = axios.create({
  baseURL: API_GATEWAY_URL,
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
  accessToken = Cookies.get('accesssToken')
  refreshToken = Cookies.get('refreshToken')
  return accessToken
}
async function getMyInfo() {
  api.defaults.headers.common = {
    Authorization: `Bearer ${accessToken}`,
  }
  const data = await api('/users/info')
  myInfo = data.userInfo
  myWorkspaces = data.workspaceList
  return data
}
function login() {
  location.href = `${LOGIN_SITE_URL}/continue=${location.href}`
}
function logout() {
  Cookies.remove('accesssToken')
  Cookies.remove('refreshToken')
  isLogin = false
  accessToken = null
  refreshToken = null
  myInfo = {}
  myWorkspaces = []
}

/**
 * 초기화
 */
async function init() {
  if (getTokensFromCookies()) {
    try {
      await getMyInfo()
      isLogin = true
    } catch (e) {
      console.error(e)
      console.log('Token is expired')
      isLogin = false
    }
  }
}

/**
 * export
 */
export default {
  get isLogin() {
    return isLogin
  },
  get accessToken() {
    return accessToken
  },
  get refreshToken() {
    return refreshToken
  },
  get myInfo() {
    return myInfo
  },
  get myWorkspaces() {
    return myWorkspaces
  },
  init,
  login,
  logout,
}
