import Store from 'stores/remote/store'
import _, { addSubscriber, removeSubscriber } from './Remote'

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
import { logger, debug } from 'utils/logger'
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
  session.on('streamCreated', event => {
    const connectionId = _.session.connection.connectionId
    event.stream.onIceStateChanged = state => {
      if (!_.session) return
      if (connectionId !== _.session.connection.connectionId) return
      if (['failed', 'disconnected', 'closed'].includes(state)) {
        Store.commit('updateParticipant', {
          connectionId: event.stream.connection.connectionId,
          status: 'disconnected',
        })
      } else if (['connected', 'completed'].includes(state)) {
        Store.commit('updateParticipant', {
          connectionId: event.stream.connection.connectionId,
          status: 'good',
        })
      } else {
        Store.commit('updateParticipant', {
          connectionId: event.stream.connection.connectionId,
          status: 'normal',
        })
      }

      logger('ice state', state)
    }
    const subscriber = session.subscribe(event.stream, '', () => {
      logger('room', 'participant subscribe successed')
      debug('room::', 'participant::', subscriber)
      addSubscriber(subscriber)
      Store.commit('updateParticipant', {
        connectionId: event.stream.connection.connectionId,
        stream: event.stream.mediaStream,
        hasVideo: event.stream.hasVideo,
        hasAudio: event.stream.hasAudio,
        video: event.stream.hasVideo
          ? event.stream.videoActive
          : event.stream.hasVideo,
      })
    })
  })
  /** session closed */
  session.on('sessionDisconnected', event => {
    if (event.reason === 'sessionClosedByServer') {
      logger('room', 'participant disconnect')
      _.clear()
      window.vue.$toasted.error(
        window.vue.$t('workspace.confirm_removed_room_leader'),
        {
          position: 'bottom-center',
          duration: 5000,
          action: {
            icon: 'close',
            onClick: (e, toastObject) => {
              toastObject.goAway(0)
            },
          },
        },
      )
      window.vue.$router.push({ name: 'workspace' })
    } else if (event.reason === 'forceDisconnectByUser') {
      logger('room', 'participant disconnect')
      _.clear()
      window.vue.$toasted.error(
        window.vue.$t('workspace.confirm_kickout_leader'),
        {
          position: 'bottom-center',
          duration: 5000,
          action: {
            icon: 'close',
            onClick: (e, toastObject) => {
              toastObject.goAway(0)
            },
          },
        },
      )
      window.vue.$router.push({ name: 'workspace' })
    } else if (event.reason === 'networkDisconnect') {
      logger('network', 'disconnect')
    }
  })
  // user leave
  session.on('streamDestroyed', event => {
    logger('room', 'stream destroy', event.reason)
    if (event.reason === 'streamNotExist') {
      const connectionId = event.stream.connection.connectionId
      Store.commit('removeStream', connectionId)
      removeSubscriber(connectionId)
    }
  })
  session.on('connectionDestroyed', event => {
    logger('room', 'participant destroy')
    const connectionId = event.connection.connectionId
    Store.commit('removeStream', connectionId)
    removeSubscriber(connectionId)
  })
  // user leave
  session.on(SIGNAL.SYSTEM, () => {
    logger('room', 'evict by system')
  })

  /** 메인뷰 변경 */
  session.on(SIGNAL.VIDEO, event => {
    window.vue.$eventBus.$emit(SIGNAL.VIDEO, event)
    // if (session.connection.connectionId === event.from.connectionId) return
    const data = JSON.parse(event.data)
    if (data.type === VIDEO.SHARE) {
      if (data.id === _.account.uuid && Store.getters['restrictedRoom']) {
        _.sendCamera(CAMERA_STATUS.CAMERA_ON)
        Store.dispatch('setDevices', {
          video: {
            isOn: true,
          },
        })
      }

      Store.dispatch('setMainView', { id: data.id, force: true })

      const participants = Store.getters['participants']
      participants.forEach(pt => {
        Store.commit('updateParticipant', {
          connectionId: pt.connectionId,
          currentWatching: data.id,
        })
      })
    } else if (data.type === VIDEO.SCREEN_SHARE) {
      const isLeader = _.account.roleType === ROLE.LEADER
      const participants = Store.getters['participants']
      const idx = participants.findIndex(
        user => user.connectionId === event.from.connectionId,
      )
      if (idx < 0) return

      const noCamera = !participants[idx].hasCamera
      const disabled = !data.enable
      const forcedView = Store.getters['viewForce'] === true
      const isSame =
        event.from.connectionId === Store.getters['mainView'].connectionId

      Store.commit('updateParticipant', {
        connectionId: event.from.connectionId,
        screenShare: data.enable,
      })

      if (!disabled) {
        Store.commit('updateParticipant', {
          connectionId: event.from.connectionId,
          hasVideo: data.enable,
        })
      }

      const releaseForcedView = [
        isLeader,
        disabled,
        forcedView,
        noCamera,
        isSame,
      ].every(condition => condition)

      if (releaseForcedView) {
        debug('screen share::', 'release forced view')
        _.sendVideo(Store.getters['mainView'].id, false)
      }

      //상대방이 더이상 보낼 스트림(카메라, PC 공유)이 없음.
      const noStream = [disabled, isSame, noCamera].every(
        condition => condition,
      )
      if (noStream) {
        Store.commit('clearMainView', event.from.connectionId)
      }

      //end of screen share
    } else {
      if (Store.getters['restrictedRoom']) {
        Store.dispatch('setMainView', {
          force: false,
          id: Store.getters['account'].uuid,
        })
      } else {
        Store.dispatch('setMainView', { force: false })

        if (data.type === VIDEO.NORMAL) {
          Store.commit('updateParticipant', {
            connectionId: event.from.connectionId,
            currentWatching: data.id,
          })
        }
      }
    }
  })
  /** 상대방 마이크 활성 정보 수신 */
  session.on(SIGNAL.MIC, event => {
    window.vue.$eventBus.$emit(SIGNAL.MIC, event)
    if (session.connection.connectionId === event.from.connectionId) return
    const data = JSON.parse(event.data)
    Store.commit('updateParticipant', {
      connectionId: event.from.connectionId,
      audio: data.isOn,
    })
  })
  /** 상대방 스피커 활성 정보 수신 */
  session.on(SIGNAL.SPEAKER, event => {
    window.vue.$eventBus.$emit(SIGNAL.SPEAKER, event)
    if (session.connection.connectionId === event.from.connectionId) return
    const data = JSON.parse(event.data)
    Store.commit('updateParticipant', {
      connectionId: event.from.connectionId,
      speaker: data.isOn,
    })
  })
  /** 플래시 컨트롤 */
  session.on(SIGNAL.FLASH, event => {
    window.vue.$eventBus.$emit(SIGNAL.FLASH, event)
    // if (session.connection.connectionId === event.from.connectionId) return
    const data = JSON.parse(event.data)
    if (data.type !== FLASH.STATUS) return
    Store.commit('deviceControl', {
      connectionId: event.from.connectionId,
      flash: data.status,
    })
  })
  /** 카메라 컨트롤(zoom) */
  session.on(SIGNAL.CAMERA, event => {
    window.vue.$eventBus.$emit(SIGNAL.CAMERA, event)
    const data = JSON.parse(event.data)
    if (data.type === CAMERA.ZOOM) {
      const track = _.publisher.stream.mediaStream.getVideoTracks()[0]
      track
        .applyConstraints({
          advanced: [
            { zoom: parseFloat(data.level) * parseFloat(_.minZoomLevel) },
          ],
        })
        .then(() => {
          // const zoom = track.getSettings().zoom // bug....
          // console.log(zoom)
          _.currentZoomLevel = parseFloat(data.level)
          _.sendCamera(
            Store.getters['video'].isOn
              ? CAMERA_STATUS.CAMERA_ON
              : CAMERA_STATUS.CAMERA_OFF,
            [event.from.connectionId],
          )
        })
      return
    }
    if (data.type === CAMERA.STATUS) {
      const params = {
        connectionId: event.from.connectionId,
        zoomLevel: parseFloat(data.currentZoomLevel),
        zoomMax: parseInt(data.maxZoomLevel),
        cameraStatus: parseInt(data.status),
        video: data.status === CAMERA_STATUS.CAMERA_ON,
      }
      if (session.connection.connectionId === event.from.connectionId) {
        _.currentZoomLevel = parseFloat(data.currentZoomLevel)
      }
      if (data.status === CAMERA_STATUS.CAMERA_NONE) {
        params.hasCamera = false
        params.hasVideo = false
      } else {
        params.hasCamera = true
        params.hasVideo = true
      }
      Store.commit('updateParticipant', params)
    }
  })
  /** 화면 해상도 설정 */
  session.on(SIGNAL.RESOLUTION, event => {
    window.vue.$eventBus.$emit(SIGNAL.RESOLUTION, event)
    if (session.connection.connectionId === event.from.connectionId) return
    Store.commit('updateResolution', {
      ...JSON.parse(event.data),
      connectionId: event.from.connectionId,
    })
  })
  /** 리더 컨트롤(pointing, local record) */
  session.on(SIGNAL.CONTROL, event => {
    window.vue.$eventBus.$emit(SIGNAL.CONTROL, event)
    // if (session.connection.connectionId === event.from.connectionId) return
    const data = JSON.parse(event.data)
    if (data.type === CONTROL.POINTING) {
      Store.dispatch('setAllow', {
        pointing: data.enable,
      })
    }

    if (data.type === CONTROL.LOCAL_RECORD) {
      Store.dispatch('setAllow', {
        localRecord: data.enable,
      })
    }

    if (data.type === CONTROL.RESTRICTED_MODE) {
      Store.commit('setRestrictedMode', data.enable)
      if (!data.enable) {
        Store.dispatch('addChat', {
          status: 'camera-control-on',
          type: 'system',
        })
      } else {
        Store.dispatch('addChat', {
          status: 'camera-control-off',
          type: 'system',
        })
      }
    }
    if (data.type === CONTROL.VIDEO) {
      const myInfo = Store.getters['myInfo']
      if (
        myInfo.cameraStatus !== CAMERA_STATUS.CAMERA_NONE &&
        Store.getters['video'].isOn !== data.enable
      ) {
        if (!myInfo.screenShare) {
          _.sendCamera(
            data.enable ? CAMERA_STATUS.CAMERA_ON : CAMERA_STATUS.CAMERA_OFF,
          )
        }
        Store.dispatch('setDevices', {
          video: {
            isOn: data.enable,
          },
        })
      }
    }
  })

  /** 채팅 수신 */
  session.on(SIGNAL.CHAT, event => {
    window.vue.$eventBus.$emit(SIGNAL.CHAT, event)
    const connectionId = event.from.connectionId
    const participants = Store.getters['participants']
    const idx = participants.findIndex(
      user => user.connectionId === connectionId,
    )
    if (idx < 0) return
    let data = JSON.parse(event.data)
    Store.commit('addChat', {
      type:
        session.connection.connectionId === event.from.connectionId
          ? 'me'
          : 'opponent',
      name: participants[idx].nickname,
      profile: participants[idx].path,
      mute: participants[idx].mute,
      connectionId: event.from.connectionId,
      text: data.text,
      languageCode: data.languageCode,
    })
  })

  /** 채팅 파일 수신 */
  session.on(SIGNAL.FILE, event => {
    window.vue.$eventBus.$emit(SIGNAL.FILE, event)
    const connectionId = event.from.connectionId
    const participants = Store.getters['participants']
    const idx = participants.findIndex(
      user => user.connectionId === connectionId,
    )
    if (idx < 0) return
    let data = JSON.parse(event.data)
    if (data.type === FILE.UPLOADED) {
      Store.commit('addChat', {
        type:
          session.connection.connectionId === event.from.connectionId
            ? 'me'
            : 'opponent',
        name: participants[idx].nickname,
        profile: participants[idx].path,
        uuid: event.from.connectionId,
        file: data.fileInfo,
      })
    }
  })
  /** LinkFlow 제어 관련 */
  session.on(SIGNAL.LINKFLOW, event => {
    const connectionId = event.from.connectionId
    const participants = Store.getters['participants']
    const idx = participants.findIndex(
      user => user.connectionId === connectionId,
    )
    if (idx < 0) return

    let data = JSON.parse(event.data)
    if (data.type === LINKFLOW.ROTATION) {
      const originConId = data.origin

      const info = {
        connectionId: originConId,
        yaw: data.yaw,
        pitch: data.pitch,
      }

      const isNotMe =
        session.connection.connectionId !== event.from.connectionId

      if (isNotMe) {
        Store.commit('updateParticipant', {
          connectionId: originConId,
          rotationPos: { yaw: data.yaw, pitch: data.pitch },
        })
      }

      window.vue.$eventBus.$emit('panoview:rotation', info)
    }
  })

  /** Pointing */
  session.on(SIGNAL.POINTING, event => {
    window.vue.$eventBus.$emit(SIGNAL.POINTING, event)
  })
  /** Drawing */
  session.on(SIGNAL.DRAWING, event => {
    window.vue.$eventBus.$emit(SIGNAL.DRAWING, event)
  })

  /** screen capture permission 수신 */
  session.on(SIGNAL.CAPTURE_PERMISSION, event => {
    window.vue.$eventBus.$emit(SIGNAL.CAPTURE_PERMISSION, event)
  })
  /** AR feature */
  session.on(SIGNAL.AR_FEATURE, event => {
    window.vue.$eventBus.$emit(SIGNAL.AR_FEATURE, event)
  })
  /** AR Pointing */
  session.on(SIGNAL.AR_POINTING, event => {
    window.vue.$eventBus.$emit(SIGNAL.AR_POINTING, event)
  })
  /** AR Drawing */
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
  if (account.uuid === uuid) {
    _.connectionId = connection.connectionId
    userObj.nickname = account.nickname
    userObj.path = account.profile
    userObj.me = true
    Store.commit('addStream', userObj)
    return 'me'
  } else {
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
