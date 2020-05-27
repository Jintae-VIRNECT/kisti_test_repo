import { getAccount, tokenRequest } from 'api/common/account'
import Cookies from 'js-cookie'
import clonedeep from 'lodash.clonedeep'
import urls from '@/server/urls'

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
function setTokensToCookies(accessToken, refreshToken) {
  Cookies.set('accessToken', accessToken)
  Cookies.set('refreshToken', refreshToken)
  return accessToken
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

export const tokenRenewal = async () => {
  // console.log('[[ TOKEN INTERVAL ]]')
  let params = {
    accessToken: accessToken,
    refreshToken: refreshToken,
  }

  const response = await tokenRequest(params)

  setTokensToCookies(response.accessToken, response.refreshToken)
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
    if (env === undefined) {
      env = 'local'
    }

    if (getTokensFromCookies()) {
      try {
        await getMyInfo(api)
        isLogin = true
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
