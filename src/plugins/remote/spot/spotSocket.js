import io from 'socket.io-client'
import { logger } from 'utils/logger'
import { URLS } from 'configs/env.config'

const BASE64_PNG_PREFIX = 'data:image/png;base64,'

export let socket = null

export let spotConnectStatus = false
let spotInitConnected = false

const initConnectStatus = () => {
  spotConnectStatus = false
  spotInitConnected = false
}

//초기 spot 서버 연결 & 연결 성공 여부 수신
export const spotServerConnect = (
  remoteId = 'REMOTE',
  onConnectionError,
  onSpotError,
) => {
  return new Promise((res, rej) => {
    const wsUrl = URLS['spot-ws'] || 'wss://192.168.6.3:3458' //'wss://192.168.6.3:3458'
    const connectOption = {
      transports: ['websocket'],
      upgrade: false,
      reconnection: false,
    }
    socket = io(wsUrl, connectOption)
    initConnectStatus()

    socket.on('connect', () => {
      logger('[SPOT]', 'SOCKET CONNECT SUCCESS')
      socket.emit('web_client_id', { remoteId }) // 유저 등록
    })
    //web_client_id에 대한 응답
    socket.on('connect_response', data => {
      //spot 서버 정상 접속 및 spot 점유 가능 상태
      if (data) {
        logger('[SPOT]', 'GOT CHANCE TO CONTROL SPOT')

        //connection error 이벤트
        socket.on('connect_error', error => {
          logger('[SPOT] connect error : ', error)
          onConnectionError(error)
        })
        //연결 끊김 이벤트
        socket.on('disconnect', reason => {
          logger('[SPOT] disconnected : ', reason)
          if (
            reason !== 'io client disconnect' &&
            reason !== 'io server disconnect'
          )
            onConnectionError(reason) //클라이언트가 직접 diconnect한 것 이외의 경우는 네트워크 에러 발생 시
        })

        //spot제어 시 발생되는 error 메시지 수신
        socket.on('spot_error_message', data => onSpotError(data))

        res(true)
      }
      //이미 spot이 다른 사용자에 의해 점유 중인 경우 - 서버 쪽에서 먼저 끊게 되어있음
      else {
        logger('[SPOT]', 'SPOT IS ALREADY UNDER CONTROL')
        //socket.disconnect()
        rej(false)
      }
    })
  })
}

export const spotServerDisconnect = () => {
  socket.disconnect()
}

//spot 연결 여부 수신
export const activateSpotConnectResponse = callback => {
  socket.on('spot_connect_response', data => {
    //spot 연결 완료
    if (data) {
      logger('[SPOT]', 'SPOT IS CONNECTED')
      spotInitConnected = true //초기 spot 연결은 성공했었음을 저장
    }
    //spot 연결 아직 안된 상태 / 연결 실패
    //else {}
    //성공 => 실패 or 실패 => 성공
    if (spotConnectStatus !== data) {
      callback(data, spotInitConnected) //문구 '연결'/'재연결'표시 여부 결정하기 위해 초기 spot 연결 성공여부를 callback에 전달
    }
    spotConnectStatus = data
  })
}

export const spotRunningStateListener = callback => {
  socket.on('running_state', data => {
    //{battery, estop, power}
    logger(data)
    callback(data)
  })
}

//카메라 이미지 수신 부
export const spotCameraImageListenerFR = callback =>
  socket.on('spot_camera_front_R', data => callback(BASE64_PNG_PREFIX + data))

export const spotCameraImageListenerFL = callback =>
  socket.on('spot_camera_front_L', data => callback(BASE64_PNG_PREFIX + data))

export const spotCameraImageListenerL = callback =>
  socket.on('spot_camera_left', data => callback(BASE64_PNG_PREFIX + data))

export const spotCameraImageListenerR = callback =>
  socket.on('spot_camera_right', data => callback(BASE64_PNG_PREFIX + data))

export const spotCameraImageListenerB = callback =>
  socket.on('spot_camera_back', data => callback(BASE64_PNG_PREFIX + data))

//spot 컨트롤 메시지 송신 부
export const spotControl = {
  estop: () => socket.emit('spot_control_estop'),
  powerOn: () => socket.emit('spot_control_power_on'),
  powerOff: () => socket.emit('spot_control_power_off'),
  sit: () => socket.emit('spot_control_sit'),
  stand: () => socket.emit('spot_control_stand'),
  drive: data => socket.emit('spot_drive_cmd', data),
}
