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
//const reRegistIntervalTime = 4 * 1000 // 4sec
const pingCheckIntervalTime = 12 * 1000 //12sec
let interval
let socket //멤버 상태 소켓
let reConnectIntervalId
let pingIntervalId
let reRegistIntervalId
//let reRegistCount = 0 //재등록 실패시 실행하는 재등록 시도 횟수 (3회 까지시도하고 중지하기 위함)
let isRegisted = false //이미 등록완료된 상태에서 멤버 상태 소켓에 재연결 방지하기 위한 플래그
let savedWorkspaceId //기존 workspaceId, workspaceId 변경 확인 하기 위함
let changingWorkspaceId //변경 요청 중인 workspaceId 변경 완료 응답 받은 후 savedWorksapceId에 저장

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
  workspaceId,
  onDuplicatedRegistrationCallback,
  onRemoteExitReceivedCallback,
  onForceLogoutReceivedCallback,
  onWorkspaceDuplicatedCallback,
  onRegistrationFailCallback,
  onWorkspaceUpdateFailCallback,
) => {
  if (window.urls && window.urls['ws']) {
    const wssUrl = window.urls['ws']

    try {
      resetReConnectInterval() //기존 재접속 인터벌 초기화
      socket = new WebSocket(`${wssUrl}/auth/status`)
    } catch (e) {
      //초기 연결 실패시 재접속 인터벌 실행
      console.error('[auth status] websocket connection error', e)
      reConnectIntervalId = setInterval(() => {
        isRegisted = false
        logger('[auth status] connect retry')
        initLoginStatus(
          workspaceId,
          onDuplicatedRegistrationCallback,
          onRemoteExitReceivedCallback,
          onForceLogoutReceivedCallback,
          onWorkspaceDuplicatedCallback,
          onRegistrationFailCallback,
          onWorkspaceUpdateFailCallback,
        )
      }, statusReconnectIntervalTime)
      return
    }

    //소켓 연결
    socket.onopen = e => {
      logger('[auth status] socket opened', e)
      //reRegistCount = 0 //재등록 실패시 재등록 시도 수 초기화

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
                '[auth status] websocket ping not received, reconnect',
              )
              socket.close()
              resetReRegistInterval() //재등록 실패 후 소켓연결이 끊긴 경우라면 재등록 시도를 초기화하기 위함
              initLoginStatus(
                workspaceId,
                onDuplicatedRegistrationCallback,
                onRemoteExitReceivedCallback,
                onForceLogoutReceivedCallback,
                onWorkspaceDuplicatedCallback,
                onRegistrationFailCallback,
                onWorkspaceUpdateFailCallback,
              ) //재접속 시도
            }, pingCheckIntervalTime)
            return
          }

          resetReRegistInterval() //재등록 실패 후 재등록 시도를 초기화

          const { code, data } = JSON.parse(e.data)
          switch (code) {
            //연결 수립 완료 메시지 수신 시 유저 정보 전송하여 등록절차를 밟는다.
            case AUTH_STATUS.CONNECT_SUCCESS:
              logger(
                '[auth status] connect success, regist : ',
                myInfo.uuid,
                myInfo.name,
                myInfo.email,
              )

              //유저 정보 등록
              sendCommand(COMMAND.REGISTER, {
                userId: myInfo.uuid,
                nickname: myInfo.name,
                email: myInfo.email,
                workspaceId,
              })

              break

            //등록완료
            case AUTH_STATUS.REGISTRATION_SUCCESS:
              isRegisted = true
              savedWorkspaceId = workspaceId // 기존 workspaceId 업데이트
              logger('[auth status] Registration Success : ', e.data)
              break

            //중복 로그인 등록 or 해당 유저 협업 중
            case AUTH_STATUS.DUPLICATED_REGISTRATION:
              isRegisted = false
              logger(
                '[auth status] Registration Fail(duplcated session exist) : ',
                e.data,
              )

              //중복된 기 접속자가 있는 경우 처리 콜백
              onDuplicatedRegistrationCallback(
                {
                  currentStatus: data.currentStatus,
                  userId: data.userId,
                  myInfo,
                },
                socket,
              )
              break

            //등록 실패 시 재시도/종료 팝업
            case AUTH_STATUS.REGISTRATION_FAIL:
              isRegisted = false
              console.error('[auth status] Registration Fail : ', e.data)
              onRegistrationFailCallback(
                {
                  userId: myInfo.uuid,
                  nickname: myInfo.name,
                  email: myInfo.email,
                  workspaceId,
                },
                socket,
              )

              // //3번까지 재등록 시도
              // if (reRegistCount < 3) {
              //   reRegistIntervalId = setInterval(() => {
              //     isRegisted = false
              //     reRegistCount++
              //     sendCommand(COMMAND.REGISTER, {
              //       userId: myInfo.uuid,
              //       nickname: myInfo.name,
              //       email: myInfo.email,
              //       workspaceId,
              //     })
              //   }, reRegistIntervalTime)
              // }
              break

            //원격 종료 요청 성공 시 (서버에서 당사자 상태 값이 변경되므로, 이 응답이 해당 유저 로그아웃 완료를 보장함)
            case AUTH_STATUS.REMOTE_EXIT_REQ_SUCCESS:
              //워스크페이스 변경에 대한 원격 종료
              if (changingWorkspaceId) {
                //workspace 변경 재요청
                sendCommand(COMMAND.WORKSPACE_UPDATE, {
                  userId: myInfo.uuid,
                  workspaceId: changingWorkspaceId,
                })
              } else {
                //유저 정보 재등록 요청
                sendCommand(COMMAND.REGISTER, {
                  userId: myInfo.uuid,
                  nickname: myInfo.name,
                  email: myInfo.email,
                  workspaceId,
                })
              }

              break

            //원격 종료 요청 실패 - 워크스페이스 되돌리기 & 재시도
            case AUTH_STATUS.REMOTE_EXIT_REQ_FAIL_NOT_FOUND:
              console.error('[auth status] Remote exit request not found')

              //재등록 혹은 재변경 요청을 실행해 정상적으로 진입 혹은 변경되거나, 중복 로그인 팝업 띄우는 케이스로 보낸다.

              //워스크페이스 변경에 대한 원격 종료
              if (changingWorkspaceId) {
                //workspace 변경 재요청
                sendCommand(COMMAND.WORKSPACE_UPDATE, {
                  userId: myInfo.uuid,
                  workspaceId: changingWorkspaceId,
                })
              } else {
                //유저 정보 재등록 요청
                sendCommand(COMMAND.REGISTER, {
                  userId: myInfo.uuid,
                  nickname: myInfo.name,
                  email: myInfo.email,
                  workspaceId,
                })
              }
              break

            //원격 종료
            case AUTH_STATUS.REMOTE_EXIT_RECEIVED:
              onRemoteExitReceivedCallback()
              break

            //워크스페이스 변경 성공
            case AUTH_STATUS.WORKSPACE_UPDATE_SUCCESS:
              savedWorkspaceId = changingWorkspaceId
              changingWorkspaceId = null
              logger('[auth status] Workspace Update Success : ', e.data)
              break

            //워스스페이스 변경 실패 - 중복 로그인
            case AUTH_STATUS.WORKSPACE_UPDATE_DUPLICATED:
              logger('[auth status] Workspace Update Duplicated : ', e.data)
              onWorkspaceDuplicatedCallback(
                {
                  currentStatus: data.currentStatus,
                  workspaceId: changingWorkspaceId,
                  oldWorkspaceId: savedWorkspaceId,
                  userId: myInfo.uuid,
                },
                socket,
              )
              break

            //워크스페이스 변경 실패 - 워크스페이스 되돌리기 & 재시도
            case AUTH_STATUS.WORKSPACE_UPDATE_FAIL:
              console.error('[auth status] Workspace update fail')
              onWorkspaceUpdateFailCallback(
                {
                  oldWorkspaceId: savedWorkspaceId,
                  workspaceId: changingWorkspaceId,
                  userId: myInfo.uuid,
                },
                socket,
              )
              changingWorkspaceId = null
              break

            //마스터 강제 로그아웃 수신
            case AUTH_STATUS.FORCE_LOGOUT_RECEIVED:
              onForceLogoutReceivedCallback()
              break

            //예외 케이스
            default:
              isRegisted = false
              console.error('[auth status] exception : ', e.data)
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
    debug('[auth status] socket close')
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
    }
    return {
      account: this.myInfo,
      workspace: this.myWorkspaces,
    }
  }
  initAuthConnection(
    workspaceId,
    onDuplicatedRegistrationCallback,
    onRemoteExitReceivedCallback,
    onForceLogoutReceivedCallback,
    onWorkspaceDuplicatedCallback,
    onRegistrationFailCallback,
    onWorkspaceUpdateFailCallback,
  ) {
    //로그인 상태 업데이트를 위한 소켓 접속 (workspace 값이 변경될 때마다 호출되나, 이미 등록 완료한 경우 실행하지 않는다)
    if (!isRegisted) {
      initLoginStatus(
        workspaceId,
        onDuplicatedRegistrationCallback,
        onRemoteExitReceivedCallback,
        onForceLogoutReceivedCallback,
        onWorkspaceDuplicatedCallback,
        onRegistrationFailCallback,
        onWorkspaceUpdateFailCallback,
      )
    }
    //workspace만 변경된 경우
    else if (isRegisted && savedWorkspaceId !== workspaceId) {
      logger(
        `[auth status] workspace changed : update member status. before: ${savedWorkspaceId}, after: ${workspaceId}`,
      )
      changingWorkspaceId = workspaceId
      //workspace 변경 요청
      sendCommand(COMMAND.WORKSPACE_UPDATE, {
        userId: myInfo.uuid,
        workspaceId,
      })
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
