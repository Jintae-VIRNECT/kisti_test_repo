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
import { COMMAND } from 'configs/push.config'
import { STATUS_SESSION_ID_SET } from '../stores/remote/mutation-types'

/**
 * 상태
 */
let isLogin = false
let myInfo = {}
let myWorkspaces = []
const intervalTime = 5 * 60 * 1000 // 5 minutes
const renewvalTime = 10 * 60 // 5 minutes
const statusReconnectIntervalTime = 4 * 1000 // 4sec
const reRegistIntervalTime = 4 * 1000 // 4sec
const pingCheckIntervalTime = 12 * 1000 //12sec
let interval
let socket //멤버 상태 소켓
let reConnectIntervalId
let pingIntervalId
let reRegistIntervalId
let reRegistCount = 0 //재등록 실패시 실행하는 재등록 시도 횟수 (3회 까지시도하고 중지하기 위함)
let isRegisted = false //이미 등록완료된 상태에서 멤버 상태 소켓에 재연결 방지하기 위한 플래그

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

//초기 소켓 연결 실패로 인한 재접속 시도 인터벌 초기화
const resetReConnectInterval = () => {
  if (reConnectIntervalId) clearInterval(reConnectIntervalId)
  reConnectIntervalId = null
}

//멤버 재등록 실패 후 재등록 시도 인터벌 초기화
const resetReRegistInterval = () => {
  if (reRegistIntervalId) clearInterval(reRegistIntervalId)
  reRegistIntervalId = null
}

//기존 PING Timeout으로 인한 재접속 시도 인터벌 초기화
const resetPingCheckInterval = () => {
  if (pingIntervalId) clearInterval(pingIntervalId)
  pingIntervalId = null
}

//사용자 상태 소켓 연결/등록
const initLoginStatus = (
  onDuplicatedRegistrationCallback,
  onRemoteExitReceivedCallback,
  onForceLogoutReceivedCallback,
) => {
  if (window.urls && window.urls['ws']) {
    const wssUrl = window.urls['ws']

    try {
      resetReConnectInterval() //기존 재접속 인터벌 초기화
      socket = new WebSocket(`${wssUrl}/auth/status`)
    } catch (e) {
      //초기 연결 실패시 재접속 인터벌 실행
      console.error('auth status websocket connection error', e)
      reConnectIntervalId = setInterval(() => {
        isRegisted = false
        logger('connect retry')
        initLoginStatus(onDuplicatedRegistrationCallback)
      }, statusReconnectIntervalTime)
      return
    }

    //소켓 연결
    socket.onopen = e => {
      logger('auth status opened', e)
      reRegistCount = 0 //재등록 실패시 재등록 시도 수 초기화

      //메시지 수신
      socket.onmessage = e => {
        //메시지, 데이터 존재 여부 판단
        if (e && e.data) {
          if (e.data === 'PING') {
            socket.send('PONG')

            resetPingCheckInterval() //기존 재접속 시도 인터벌 취소

            //ping 메시지 기다리고, 메시지 오지 않올시 재접속 시도
            pingIntervalId = setInterval(() => {
              isRegisted = false
              console.error(
                'auth status websocket ping not received, reconnect',
              )
              socket.close()
              resetReRegistInterval() //재등록 실패 후 소켓연결이 끊긴 경우라면 재등록 시도를 초기화하기 위함
              initLoginStatus(onDuplicatedRegistrationCallback) //재접속 시도
            }, pingCheckIntervalTime)
            return
          }

          resetReRegistInterval() //재등록 실패 후 재등록 시도를 초기화

          const { code, data } = JSON.parse(e.data)
          switch (code) {
            //연결 수립 완료 메시지 수신 시 유저 정보 전송하여 등록절차를 밟는다.
            case AUTH_STATUS.CONNECT_SUCCESS:
              logger(
                'connect success, regist : ',
                myInfo.uuid,
                myInfo.name,
                myInfo.email,
              )

              //유저 정보 등록
              sendCommand(COMMAND.REGISTER, {
                uuid: myInfo.uuid,
                name: myInfo.name,
                email: myInfo.email,
              })
              break

            //등록완료
            case AUTH_STATUS.REGISTRATION_SUCCESS:
              isRegisted = true
              logger('Registration Success : ', e.data)
              //session id 저장 : 중복 로그인으로 인한 로그아웃 요청 수신 시 로그아웃 시킬 기기 구분하기 위해 사용
              window.vue.$store.commit(STATUS_SESSION_ID_SET, data.sessionId)
              break

            //중복 로그인 등록 or 해당 유저 협업 중
            case AUTH_STATUS.DUPLICATED_REGISTRATION:
              isRegisted = false
              logger('Registration Fail(duplcated session exist) : ', e.data)

              //session id 저장 : 기접속자 로그아웃 요청 시 로그아웃 시킬 기기 구분하기 위해 저장
              window.vue.$store.commit(STATUS_SESSION_ID_SET, data.sessionId)

              //중복된 기 접속자가 있는 경우 처리 콜백
              onDuplicatedRegistrationCallback(
                {
                  currentStatus: data.currentStatus,
                  sessionId: data.sessionId,
                  userId: data.userUUID,
                  myInfo,
                },
                socket,
              )
              break

            //등록 실패 시 재등록 시도
            case AUTH_STATUS.REGISTRATION_FAIL:
              isRegisted = false
              console.error('Registration Fail : ', e.data)

              //3번까지 재등록 시도
              if (reRegistCount < 3) {
                reRegistIntervalId = setInterval(() => {
                  isRegisted = false
                  reRegistCount++
                  sendCommand(COMMAND.REGISTER, {
                    uuid: myInfo.uuid,
                    name: myInfo.name,
                    email: myInfo.email,
                  })
                }, reRegistIntervalTime)
              }
              break

            //원격 종료 요청 성공 시 (서버에서 당사자 상태 값이 변경되므로, 이 응답이 해당 유저 로그아웃 완료를 보장함)
            case AUTH_STATUS.REMOTE_EXIT_REQ_SUCCESS:
              //자신이 보낸 요청에 대한 응답인지를 체크한 후 재등록 실행한다
              if (
                window.vue.$store.getters.statusSessionId === data.sessionId
              ) {
                //유저 정보 재등록 요청
                sendCommand(COMMAND.REGISTER, {
                  uuid: myInfo.uuid,
                  name: myInfo.name,
                  email: myInfo.email,
                })
              }
              break

            //원격 종료 요청 실패
            case AUTH_STATUS.REMOTE_EXIT_REQ_FAIL_NOT_FOUND:
              console.error('Remote exit request not found')
              break

            //원격 종료
            case AUTH_STATUS.REMOTE_EXIT_RECEIVED:
              onRemoteExitReceivedCallback()
              break

            //마스터 강제 로그아웃 수신
            case AUTH_STATUS.FORCE_LOGOUT_RECEIVED:
              onForceLogoutReceivedCallback()
              break

            //예외 케이스
            default:
              isRegisted = false
              console.error('auth status exception : ', e.data)
          }
        }
      }
    }
  }
}

const sendCommand = (command, data) => {
  const message = {
    command,
    data,
  }

  socket.send(JSON.stringify(message))
}

//사용자 상태 소켓 연결 해제
const endLoginStatus = () => {
  if (socket) {
    debug('auth status socket close')
    isRegisted = false
    resetPingCheckInterval()
    resetReConnectInterval()
    resetReRegistInterval()

    window.vue.$store.commit(STATUS_SESSION_ID_SET, '')
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

  /**
   * @param {Function} onDuplicatedRegistrationCallback - auth/status에 접속 시 중복 세션이 있는 경우 콜백함수
   * @param {Function} onRemoteExitReceivedCallback - 중복 세션 원격종료 수신시 콜백함수
   * @param {Function} onForceLogoutReceivedCallback - 마스터에 의한 강제로그아웃 수신시 콜백함수
   * @returns { account, workspace }
   */
  async init(
    onDuplicatedRegistrationCallback,
    onRemoteExitReceivedCallback,
    onForceLogoutReceivedCallback,
  ) {
    if (Cookies.get('accessToken')) {
      try {
        await getMyInfo()
        isLogin = true
        tokenRenewal()
        if (!isRegisted)
          initLoginStatus(
            onDuplicatedRegistrationCallback,
            onRemoteExitReceivedCallback,
            onForceLogoutReceivedCallback,
          ) //로그인 상태 업데이트를 위한 소켓 접속 (workspace 진행 시마다 init이 호출되나, 이미 등록완료한 경우 실행하지 않는다)
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
  logout(redirect = true) {
    endLoginStatus() //로그아웃 상태 업데이트
    cookieClear()
    isLogin = false
    myInfo = {}
    myWorkspaces = []
    if (redirect)
      location.href = `${URLS['console']}/?continue=${location.href}`
    return this
  }
}

const auth = new Auth()
export default auth
