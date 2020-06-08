import { getAccount, tokenRequest } from 'api/common'
import Cookies from 'js-cookie'
import clonedeep from 'lodash.clonedeep'
import urls from '@/server/urls'
import jwtDecode from 'jwt-decode'

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
function setTokensToCookies(response) {
  Cookies.set('accessToken', response.accessToken)
  Cookies.set('refreshToken', response.refreshToken)
  accessToken = response.accessToken
  refreshToken = response.refreshToken
  api.defaults.headers.common = {
    Authorization: `Bearer ${accessToken}`,
  }
}

const tokenInterval = async () => {
  let expireTime = jwtDecode(accessToken).exp,
    now = parseInt(Date.now() / 1000)

  // check expire time
  if (expireTime - now < renewvalTime) {
    let params = {
      accessToken: accessToken,
      refreshToken: refreshToken,
    }

    let response = await tokenRequest(params)

    setTokensToCookies(response)
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
  try {
    const res = await getAccount()
    myInfo = res.userInfo
    myWorkspaces = res.workspaceInfoList
    return res
  } catch (err) {
    if (err.code === 9999) {
      // console.log('9999')
      // return
    }
    throw err
  }
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

  async init() {
    env = process.env.TARGET_ENV
    console.log(env)
    if (env === undefined) {
      env = 'local'
    }

    if (getTokensFromCookies()) {
      try {
        await getMyInfo(api)
        isLogin = true
        tokenRenewal()
      } catch (e) {
        console.log(e)
        console.error('Token is expired')
        isLogin = false
      }
    }
    return this
  }
  login(options = {}) {
    const url = options.LOGIN_SITE_URL
      ? options.LOGIN_SITE_URL
      : urls.console[env]
    location.href = `${url}/?continue=${location.href}`
    return this
  }
  logout(options = {}) {
    Cookies.remove('accessToken')
    Cookies.remove('refreshToken')
    isLogin = false
    accessToken = null
    refreshToken = null
    myInfo = {}
    myWorkspaces = []
    const url = options.LOGIN_SITE_URL
      ? options.LOGIN_SITE_URL
      : urls.console[env]
    location.href = `${url}/?continue=${location.href}`
    return this
  }
}

const auth = new Auth()
export default auth
