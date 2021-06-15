import Store from 'stores/remote/store'
import _ from './Remote'
import eventListener from './RemoteSessionEventListener'

// import { SIGNAL, CONTROL, ROLE } from 'configs/remote.config'

import {
  SIGNAL,
  CONTROL,
  CAMERA,
  FLASH,
  ROLE,
  VIDEO,
  FILE,
  LINKFLOW,
  LOCATION,
} from 'configs/remote.config'

import {
  FLASH as FLASH_STATUS,
  CAMERA as CAMERA_STATUS,
} from 'configs/device.config'

import { getUserInfo } from 'api/http/account'
import { logger } from 'utils/logger'
import { checkInput } from 'utils/deviceCheck'

export const addSessionEventListener = session => {
  let loading = false
  session.on('connectionCreated', event => {
    logger('room', 'connection created')
    const user = setUserObject(event)

    if (user === 'me') return
    setTimeout(() => {
      // send default signals
      if (_.publisher) {
        _.sendMic(Store.getters['mic'].isOn, [event.connection.connectionId])
        _.sendSpeaker(Store.getters['speaker'].isOn, [
          event.connection.connectionId,
        ])
        _.sendResolution(null, [event.connection.connectionId])
        _.sendFlashStatus(FLASH_STATUS.FLASH_NONE, [
          event.connection.connectionId,
        ])
      }
      if (_.account.roleType === ROLE.LEADER) {
        _.sendControl(CONTROL.POINTING, Store.getters['allowPointing'], [
          event.connection.connectionId,
        ])
        _.sendControl(CONTROL.LOCAL_RECORD, Store.getters['allowLocalRecord'], [
          event.connection.connectionId,
        ])

        const viewForce = Store.getters['viewForce'] === true ? true : false
        _.sendVideo(Store.getters['mainView'].id, viewForce, [
          event.connection.connectionId,
        ])

        if (Store.getters['view'] === 'drawing') {
          window.vue.$eventBus.$emit(
            'participantChange',
            event.connection.connectionId,
          )
        }
        if (Store.getters['restrictedRoom']) {
          _.sendControlRestrict('video', !Store.getters['allowCameraControl'], [
            event.connection.connectionId,
          ])
        }
      }
      if (Store.getters['myInfo'].screenShare) {
        _.sendScreenSharing(true, [event.connection.connectionId])
      }

      if (_.publisher) {
        checkInput({ video: true, audio: false }).then(info => {
          const hasVideo = info.hasVideo
          if (hasVideo) {
            if (_.publisher.stream.hasVideo) {
              _.sendCamera(
                Store.getters['video'].isOn
                  ? CAMERA_STATUS.CAMERA_ON
                  : CAMERA_STATUS.CAMERA_OFF,
                [event.connection.connectionId],
              )
            }
          } else {
            _.sendCamera(CAMERA_STATUS.CAMERA_NONE, [
              event.connection.connectionId,
            ])
          }
        })
      } else {
        _.sendCamera(CAMERA_STATUS.CAMERA_NONE, [event.connection.connectionId])
      }
    }, 300)
  })

  session.on('streamCreated', eventListener.streamCreated)

  /** session closed */
  session.on('sessionDisconnected', eventListener.sessionDisconnected)

  // user leave
  session.on('streamDestroyed', eventListener.streamDestroyed)

  // user leave
  session.on('connectionDestroyed', eventListener.connectionDestroyed)

  // user leave by system
  session.on(SIGNAL.SYSTEM, () => {
    logger('room', 'evict by system')
  })

  /** 메인뷰 변경 */

  session.on(SIGNAL.VIDEO, eventListener.signalVideo)

  // session.on(SIGNAL.VIDEO, event => {
  //   window.vue.$eventBus.$emit(SIGNAL.VIDEO, event)
  //   // if (session.connection.connectionId === event.from.connectionId) return
  //   const data = JSON.parse(event.data)
  //   if (data.type === VIDEO.SHARE) {
  //     if (data.id === _.account.uuid && Store.getters['restrictedRoom']) {
  //       _.sendCamera(CAMERA_STATUS.CAMERA_ON)
  //       Store.dispatch('setDevices', {
  //         video: {
  //           isOn: true,
  //         },
  //       })
  //     }

  //     Store.dispatch('setMainView', { id: data.id, force: true })

  //     const participants = Store.getters['participants']
  //     participants.forEach(pt => {
  //       Store.commit('updateParticipant', {
  //         connectionId: pt.connectionId,
  //         currentWatching: data.id,
  //       })
  //     })
  //   } else if (data.type === VIDEO.SCREEN_SHARE) {
  //     const isLeader = _.account.roleType === ROLE.LEADER
  //     const participants = Store.getters['participants']
  //     const idx = participants.findIndex(
  //       user => user.connectionId === event.from.connectionId,
  //     )
  //     if (idx < 0) return

  //     const noCamera = !participants[idx].hasCamera
  //     const disabled = !data.enable
  //     const forcedView = Store.getters['viewForce'] === true
  //     const isSame =
  //       event.from.connectionId === Store.getters['mainView'].connectionId

  //     Store.commit('updateParticipant', {
  //       connectionId: event.from.connectionId,
  //       screenShare: data.enable,
  //     })

  //     if (!disabled) {
  //       Store.commit('updateParticipant', {
  //         connectionId: event.from.connectionId,
  //         hasVideo: data.enable,
  //       })
  //     }

  //     const releaseForcedView = [
  //       isLeader,
  //       disabled,
  //       forcedView,
  //       noCamera,
  //       isSame,
  //     ].every(condition => condition)

  //     if (releaseForcedView) {
  //       debug('screen share::', 'release forced view')
  //       _.sendVideo(Store.getters['mainView'].id, false)
  //     }

  //     //상대방이 더이상 보낼 스트림(카메라, PC 공유)이 없음.
  //     const noStream = [disabled, isSame, noCamera].every(
  //       condition => condition,
  //     )
  //     if (noStream) {
  //       Store.commit('clearMainView', event.from.connectionId)
  //     }

  //     //end of screen share
  //   } else {
  //     if (Store.getters['restrictedRoom']) {
  //       Store.dispatch('setMainView', {
  //         force: false,
  //         id: Store.getters['account'].uuid,
  //       })
  //     } else {
  //       Store.dispatch('setMainView', { force: false })

  //       if (data.type === VIDEO.NORMAL) {
  //         Store.commit('updateParticipant', {
  //           connectionId: event.from.connectionId,
  //           currentWatching: data.id,
  //         })
  //       }
  //     }
  //   }
  // })

  /** 상대방 마이크 활성 정보 수신 */
  session.on(SIGNAL.MIC, eventListener.signalMic)

  /** 상대방 스피커 활성 정보 수신 */
  session.on(SIGNAL.SPEAKER, eventListener.signalSpeaker)

  /** 플래시 컨트롤 */
  session.on(SIGNAL.FLASH, eventListener.signalFlash)

  /** 카메라 컨트롤(zoom) */
  session.on(SIGNAL.CAMERA, eventListener.signalCamera)

  /** 화면 해상도 설정 */
  session.on(SIGNAL.RESOLUTION, eventListener.signalResolution)

  /** 리더 컨트롤(pointing, local record) */
  session.on(SIGNAL.CONTROL, eventListener.signalControl)

  /** 채팅 수신 */
  session.on(SIGNAL.CHAT, eventListener.signalChat)

  /** 채팅 파일 수신 */
  session.on(SIGNAL.FILE, eventListener.signalChatFile)

  /** LinkFlow 제어 관련 */
  session.on(SIGNAL.LINKFLOW, eventListener.signalLinkFlow)

  /** Pointing */
  session.on(SIGNAL.POINTING, eventListener.signalPointing)

  /** Drawing */
  session.on(SIGNAL.DRAWING, eventListener.signalDrawing)

  /** screen capture permission 수신 */
  session.on(SIGNAL.CAPTURE_PERMISSION, eventListener.signalCapturePermission)

  /** AR feature */
  session.on(SIGNAL.AR_FEATURE, eventListener.signalArFeature)

  /** AR Pointing */
  session.on(SIGNAL.AR_POINTING, eventListener.signalArPointing)

  /** AR Drawing */

  // session.on(SIGNAL.AR_DRAWING, eventListener.signalArDrawing)

  session.on(SIGNAL.AR_DRAWING, event => {
    window.vue.$eventBus.$emit(SIGNAL.AR_DRAWING, event)
  })

  /** 위치 정보*/
  session.on(SIGNAL.LOCATION, event => {
    const connectionId = event.from.connectionId
    const participants = Store.getters['participants']
    const idx = participants.findIndex(
      user => user.connectionId === connectionId,
    )
    if (idx < 0) return

    const data = JSON.parse(event.data)

    if (data.type === LOCATION.RESPONSE) {
      //위치 요청 동의 / 거부 관련 처리
      window.vue.$eventBus.$emit('map:enable', data.enable)
    } else if (data.type === LOCATION.INFO) {
      //위치 정보
      const location = { lat: data.lat, lng: data.lon }
      window.vue.$eventBus.$emit('map:location', location)
    } else if (data.type === LOCATION.STOPPED) {
      if (data.reason === 'GPSoff') {
        //위치 정보 공유 중단 by GPS off
        window.vue.$eventBus.$emit('map:gpsoff')
      } else if (data.reason === 'BadSignal') {
        window.vue.$eventBus.$emit('map:timeout')
      } else {
        window.vue.$eventBus.$emit('map:close')
      }
    }
  })
}

/**
 * 협업에 사용되는 유저 객체 생성
 * @param {Object} event 이벤트 객체
 * @returns {String} 자기 자신 or 다른 참가자 여부
 */
const setUserObject = event => {
  let userObj
  let connection = event.connection

  const metaData = JSON.parse(connection.data.split('%/%')[0])

  let uuid = metaData.clientData
  let roleType = metaData.roleType
  let deviceType = metaData.deviceType

  userObj = {
    id: uuid,
    // stream: stream.mediaStream,
    stream: null,
    connectionId: connection.connectionId,
    // nickname: participant.nickname,
    // path: participant.profile,
    nickname: '',
    path: null,
    video: false,
    audio: false,
    hasVideo: false,
    hasAudio: false,
    hasCamera: false,
    speaker: true,
    mute: false,
    status: 'normal',
    roleType: roleType,
    deviceType: deviceType,
    permission: 'default',
    hasArFeature: false,
    cameraStatus: 'default',
    zoomLevel: 1, // zoom 레벨
    zoomMax: 1, // zoom 최대 레벨
    flash: 'default', // flash 제어
    rotationPos: null, //pano view의 회전 좌표
    screenShare: false,
    currentWatching: uuid, //현재 자신이 보고있는 참가자 uuid
  }
  const account = Store.getters['account']

  //자기 자신인 경우
  if (account.uuid === uuid) {
    _.connectionId = connection.connectionId
    userObj.nickname = account.nickname
    userObj.path = account.profile
    userObj.me = true
    Store.commit('addStream', userObj)
    return 'me'
  } else {
    //자신을 제외한 나머지 유저
    logger('room', "participant's connected")
    Store.commit('addStream', userObj)
    getUserInfo({
      userId: userObj.id,
    }).then(participant => {
      const params = {
        connectionId: event.connection.connectionId,
        nickname: participant.nickname,
        path: participant.profile,
      }
      Store.commit('updateParticipant', params)
    })
    return 'participant'
  }

  // return streamObj
}
