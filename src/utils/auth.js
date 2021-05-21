import { getAccount, tokenRequest, getSettingInfo } from 'api/http/account'
import Cookies from 'js-cookie'
import clonedeep from 'lodash.clonedeep'
import jwtDecode from 'jwt-decode'
import { setHttpOptions } from 'api/gateway/gateway'
import axios from 'api/axios'
import { logger, debug } from 'utils/logger'
import {
  setConfigs,
  setUrls,
  RUNTIME_ENV,
  RUNTIME,
  URLS,
} from 'configs/env.config'
import { AUTH_STATUS } from 'configs/status.config'

/**
 * 상태
 */
let isLogin = false
let myInfo = {}
let myWorkspaces = []
const intervalTime = 5 * 60 * 1000 // 5 minutes
const renewvalTime = 10 * 60 // 5 minutes
let interval
let socket

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

//사용자 상태 소켓 연결/등록
const initLoginStatus = () => {
  if (window.urls && window.urls['ws']) {
    const wssUrl = window.urls['ws']

    try {
      socket = new WebSocket(`${wssUrl}/auth/status`)
      socket.onerror = e => logger(e)
    } catch (e) {
      console.error('Websocket connection error')
      return
    }

    //소켓 연결
    socket.onopen = e => {
      logger('auth status opened', e)

      //메시지 수신
      socket.onmessage = e => {
        //debug('message received', e.data)

        //메시지, 데이터 존재 여부 판단
        if (e && e.data) {
          if (e.data === 'PING') {
            socket.send('PONG')
            return
          }

          const { code } = JSON.parse(e.data)

          switch (code) {
            //연결 수립 완료 메시지 수신 시 유저 정보 전송하여 등록절차를 밟는다.
            case AUTH_STATUS.CONNECT_SUCCESS:
              logger(
                'connect success, regist',
                myInfo.uuid,
                myInfo.name,
                myInfo.email,
              )
              socket.send(
                JSON.stringify({
                  uuid: myInfo.uuid,
                  name: myInfo.name,
                  email: myInfo.email,
                }),
              )
              break
            //등록완료
            case AUTH_STATUS.REGISTRATION_SUCCESS:
              logger('Registration Success', e.data)
              break
            //등록 실패 및 예외 케이스
            default:
              new Error(e.data)
          }
        }
      }
    }
  }
}

//사용자 상태 소켓 연결 해제
const endLoginStatus = () => {
  if (socket) {
    debug('auth status socket close')
    socket.close()
  }
}

export const getConfigs = async () => {
  if (window.urls && window.urls['api']) return
  const res = await axios.get(
    `${location.origin}/configs?origin=${location.hostname}`,
  )

  const RUNTIME_ENV = res.data.RUNTIME || 'production'
  // const runtimeEnv = 'onpremise'
  logger('RUNTIME ENV', res.data.RUNTIME)
  delete res.data.RUNTIME

  const TIMEOUT = res.data.TIMEOUT || 5000
  delete res.data.TIMEOUT

  const ALLOW_NO_AUDIO = res.data.ALLOW_NO_AUDIO || false
  delete res.data.ALLOW_NO_AUDIO

  const ALLOW_NO_DEVICE = res.data.ALLOW_NO_DEVICE || false
  delete res.data.ALLOW_NO_DEVICE

  debug('URLS::', res.data)

  setHttpOptions(res.data['api'], TIMEOUT)
  window.urls = res.data
  setConfigs({
    RUNTIME_ENV,
    TIMEOUT,
    ALLOW_NO_AUDIO,
    ALLOW_NO_DEVICE,
  })
  setUrls(res.data)
}

export const getSettings = async () => {
  if (RUNTIME_ENV !== RUNTIME.ONPREMISE) {
    document.title = `VIRNECT | Remote`
    return
  }
  try {
    const settings = await getSettingInfo()
    document.title = `${settings.workspaceTitle} | Remote`
    const favicon = document.querySelector("link[rel*='icon']")
    favicon.href = settings.favicon

    setConfigs({
      whiteLogo: settings.whiteLogo,
      defaultLogo: settings.defaultLogo,
    })
  } catch (err) {}
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

  async check() {
    await getConfigs()

    if (Cookies.get('accessToken')) {
      return true
    }
    return false
  }

  async init() {
    if (Cookies.get('accessToken')) {
      try {
        await getMyInfo()
        isLogin = true
        tokenRenewal()
      } catch (e) {
        console.error('Token is expired')
        isLogin = false
      }

      initLoginStatus() //로그인 상태 업데이트
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
  logout() {
    endLoginStatus() //로그아웃 상태 업데이트
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
