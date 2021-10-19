import {
  getAccount,
  tokenRequest,
  getSettingInfo,
  logout,
} from 'api/http/account'
import Cookies from 'js-cookie'
import clonedeep from 'lodash.clonedeep'
import jwtDecode from 'jwt-decode'
import { setHttpOptions } from 'api/gateway/gateway'
import axios from 'api/axios'
import { logger, debug } from 'utils/logger'
import {
  setConfigs,
  setUrls,
  setSettings,
  RUNTIME_ENV,
  RUNTIME,
  URLS,
} from 'configs/env.config'

/**
 * 상태
 */
let isLogin = false
let myInfo = {}
let myWorkspaces = []
const intervalTime = 5 * 60 * 1000 // 5 minutes
const renewvalTime = 10 * 60 // 5 minutes
let interval

/**
 * 메소드
 */
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
  debug('TOKEN::', axios.defaults.headers)
}

const tokenInterval = async () => {
  const accessToken = Cookies.get('accessToken')
  let expireTime = jwtDecode(accessToken).exp,
    now = parseInt(Date.now() / 1000)

  // check expire time
  debug('TOKEN RENEWVAL TIME::', expireTime - now)
  if (expireTime - now < renewvalTime) {
    let params = {
      accessToken: accessToken,
      refreshToken: Cookies.get('refreshToken'),
    }

    let response = await tokenRequest(params)

    if (response.refreshed === true) {
      setTokensToCookies(response)
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

const getMyInfo = async () => {
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

const getConfigs = async () => {
  if (window.urls && window.urls['api']) return
  const res = await axios.get(
    `${location.origin}/configs?origin=${location.hostname}`,
  )

  const runtimeEnv = res.data.runtime || 'production'
  // const runtimeEnv = 'onpremise'
  logger('RUNTIME ENV', res.data.runtime)
  delete res.data.runtime

  const timeout = res.data.timeout || 10000
  delete res.data.timeout

  debug('URLS::', res.data)

  setHttpOptions(res.data['api'], timeout)

  window.urls = res.data

  if (res.data.settings) {
    window.settings = res.data.settings
    setSettings(res.data.settings)
  } else {
    setSettings({})
  }

  setConfigs({
    runtimeEnv,
  })

  setUrls(res.data)
}

const getWsSettings = async () => {
  if (RUNTIME_ENV !== RUNTIME.ONPREMISE) {
    document.title = `VIRNECT | Dashboard`
  } else {
    const settings = await getSettingInfo()

    document.title = `${settings.workspaceTitle} | Dashboard`
    const favicon = document.querySelector("link[rel*='icon']")
    favicon.href = settings.favicon

    setConfigs({
      whiteLogo: settings.whiteLogo,
      defaultLogo: settings.defaultLogo,
    })
  }
}

export const cookieClear = () => {
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
  get myInfo() {
    return clonedeep(myInfo)
  }
  get myWorkspaces() {
    return clonedeep(myWorkspaces)
  }

  async init() {
    await getConfigs()

    if (Cookies.get('accessToken')) {
      try {
        await Promise.all([getMyInfo(), getWsSettings()])

        isLogin = true
        tokenRenewal()
      } catch (e) {
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
    location.href = `${URLS['console']}/?continue=${location.href}`
    return this
  }
  async logout() {
    await logout({ uuid: myInfo.uuid, accessToken: Cookies.get('accessToken') })
    cookieClear()
    isLogin = false
    myInfo = {}
    myWorkspaces = []
    location.href = `${URLS['console']}/?continue=${location.href}`
    return this
  }
}

const auth = new Auth()
export default auth
