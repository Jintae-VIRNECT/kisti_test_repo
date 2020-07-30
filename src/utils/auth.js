import { getAccount, tokenRequest } from 'api/common'
import Cookies from 'js-cookie'
import clonedeep from 'lodash.clonedeep'
import jwtDecode from 'jwt-decode'
import { setAuthorization, setBaseURL } from 'api/gateway/gateway'
import axios from 'api/axios'
import { debug } from 'utils/logger'

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
const renewvalTime = 10 * 60 // 5 minutes
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
  const cookieOption = {
    expires: response.expireIn / 3600000,
    domain:
      location.hostname.split('.').length === 3
        ? location.hostname.replace(/.*?\./, '')
        : location.hostname,
  }
  Cookies.set('accessToken', response.accessToken, cookieOption)
  Cookies.set('refreshToken', response.refreshToken, cookieOption)
  accessToken = response.accessToken
  refreshToken = response.refreshToken
  setAuthorization(accessToken)
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

async function getUrls() {
  const res = await axios.get(`${location.origin}/urls`)

  debug('URLS::', res.data)

  setBaseURL(res.data.api)
  window.urls = res.data
  return res.data
}

const cookieClear = () => {
  if (/\.?virnect\.com/.test(location.href)) {
    Cookies.remove('accessToken', { domain: '.virnect.com' })
    Cookies.remove('refreshToken', { domain: '.virnect.com' })
  } else {
    Cookies.remove('accessToken')
    Cookies.remove('refreshToken')
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
    if (env === undefined) {
      env = 'local'
    }
    await getUrls()

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
    return {
      account: this.myInfo,
      workspace: this.myWorkspaces,
    }
  }
  login() {
    cookieClear()
    const url = window.urls.console
    location.href = `${url}/?continue=${location.href}`
    return this
  }
  logout() {
    cookieClear()
    isLogin = false
    accessToken = null
    refreshToken = null
    myInfo = {}
    myWorkspaces = []
    const url = window.urls.console
    location.href = `${url}/?continue=${location.href}`
    return this
  }
}

const auth = new Auth()
export default auth
