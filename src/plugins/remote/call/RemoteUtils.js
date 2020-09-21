import Store from 'stores/remote/store'
import _, { addSubscriber, removeSubscriber } from './Remote'

import {
  SIGNAL,
  CONTROL,
  CAMERA,
  FLASH,
  ROLE,
  VIDEO,
} from 'configs/remote.config'
import {
  FLASH as FLASH_STATUE,
  CAMERA as CAMERA_STATUS,
} from 'configs/device.config'

import { getUserInfo } from 'api/http/account'
import { logger, debug } from 'utils/logger'

export const addSessionEventListener = session => {
  session.on('connectionCreated', event => {
    logger('room', 'connection created')
    setUserObject(event)
  })
  session.on('streamCreated', event => {
    const subscriber = session.subscribe(event.stream, '', () => {
      logger('room', 'participant subscribe successed')
      debug('room::', 'participant::', subscriber)
      Store.commit('updateParticipant', {
        connectionId: event.stream.connection.connectionId,
        stream: event.stream.mediaStream,
        hasVideo: event.stream.hasVideo,
        video: event.stream.hasVideo
          ? event.stream.videoActive
          : event.stream.hasVideo,
      })
      addSubscriber(subscriber)
      _.sendResolution(null, [event.stream.connection.connectionId])
      _.video(Store.getters['video'].isOn, [
        event.stream.connection.connectionId,
      ])
      _.mic(Store.getters['mic'].isOn, [event.stream.connection.connectionId])
      _.speaker(Store.getters['speaker'].isOn, [
        event.stream.connection.connectionId,
      ])
      _.flashStatus(FLASH_STATUE.FLASH_NONE, [
        event.stream.connection.connectionId,
      ])
      if (_.account.roleType === ROLE.LEADER) {
        _.control(CONTROL.POINTING, Store.getters['allowPointing'], [
          event.stream.connection.connectionId,
        ])
        _.control(CONTROL.LOCAL_RECORD, Store.getters['allowLocalRecord'], [
          event.stream.connection.connectionId,
        ])
        if (Store.getters['viewForce'] === true) {
          _.mainview(Store.getters['mainView'].id, true, [
            event.stream.connection.connectionId,
          ])
        }
        if (Store.getters['view'] === 'drawing') {
          window.vue.$eventBus.$emit(
            'participantChange',
            event.stream.connection.connectionId,
          )
        }
      }
    })
  })
  /** session closed */
  session.on('sessionDisconnected', event => {
    logger('room', 'participant disconnect')
    _.clear()
    if (event.reason === 'sessionClosedByServer') {
      // TODO: MESSAGE
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
      // TODO: MESSAGE
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
    }
  })
  // user leave
  session.on('streamDestroyed', event => {
    logger('room', 'participant destroy')
    const connectionId = event.stream.connection.connectionId
    Store.commit('removeStream', connectionId)
    removeSubscriber(event.stream.streamId)
  })
  // user leave
  session.on(SIGNAL.SYSTEM, () => {
    logger('room', 'evict by system')
  })

  /** 메인뷰 변경 */
  session.on(SIGNAL.VIDEO, event => {
    if (session.connection.connectionId === event.from.connectionId) return
    const data = JSON.parse(event.data)
    if (data.type === VIDEO.SHARE) {
      Store.dispatch('setMainView', { id: data.id, force: true })
    } else {
      Store.dispatch('setMainView', { force: false })
    }
  })
  /** 상대방 마이크 활성 정보 수신 */
  session.on(SIGNAL.MIC, event => {
    if (session.connection.connectionId === event.from.connectionId) return
    const data = JSON.parse(event.data)
    Store.commit('updateParticipant', {
      connectionId: event.from.connectionId,
      audio: data.isOn,
    })
  })
  /** 상대방 스피커 활성 정보 수신 */
  session.on(SIGNAL.SPEAKER, event => {
    if (session.connection.connectionId === event.from.connectionId) return
    const data = JSON.parse(event.data)
    Store.commit('updateParticipant', {
      connectionId: event.from.connectionId,
      speaker: data.isOn,
    })
  })
  /** 플래시 컨트롤 */
  session.on(SIGNAL.FLASH, event => {
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
          _.video(true, [event.from.connectionId])
        })
      return
    }
    if (data.type === CAMERA.STATUS) {
      if (session.connection.connectionId === event.from.connectionId) {
        _.currentZoomLevel = parseFloat(data.currentZoomLevel)
      }
      Store.commit('deviceControl', {
        connectionId: event.from.connectionId,
        zoomLevel: parseFloat(data.currentZoomLevel),
        zoomMax: parseInt(data.maxZoomLevel),
        cameraStatus: parseInt(data.status),
        video: data.status === CAMERA_STATUS.CAMERA_ON,
      })
    }
  })
  /** 화면 해상도 설정 */
  session.on(SIGNAL.RESOLUTION, event => {
    if (session.connection.connectionId === event.from.connectionId) return
    Store.commit('updateResolution', {
      ...JSON.parse(event.data),
      connectionId: event.from.connectionId,
    })
  })
  /** 리더 컨트롤(pointing, local record) */
  session.on(SIGNAL.CONTROL, event => {
    // if (session.connection.connectionId === event.from.connectionId) return
    const data = JSON.parse(event.data)
    if (data.type === CONTROL.POINTING) {
      Store.dispatch('setAllow', {
        pointing: data.enable,
      })
    } else if (data.type === CONTROL.LOCAL_RECORD) {
      Store.dispatch('setAllow', {
        localRecord: data.enable,
      })
    }
  })
  /** screen capture permission 수신 */
  // session.on(SIGNAL.CAPTURE_PERMISSION, event => {
  //   const data = JSON.parse(event.data)
  //   if (data.type === CAPTURE_PERMISSION.RESPONSE) {
  //     Store.commit('updateParticipant', {
  //       connectionId: event.from.connectionId,
  //       permission: data.isAllowed,
  //     })
  //   }
  // })

  /** 채팅 수신 */
  session.on(SIGNAL.CHAT, event => {
    const connectionId = event.from.connectionId
    const participants = Store.getters['participants']
    const idx = participants.findIndex(
      user => user.connectionId === connectionId,
    )
    if (idx < 0) return
    let data = event.data
    Store.commit('addChat', {
      type:
        session.connection.connectionId === event.from.connectionId
          ? 'me'
          : 'opponent',
      name: participants[idx].nickname,
      profile: participants[idx].path,
      connectionId: event.from.connectionId,
      text: data.replace(/\</g, '&lt;'),
    })
  })

  /** 채팅 파일 수신 */
  session.on(SIGNAL.FILE, event => {
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
      uuid: event.from.connectionId,
      file: data,
    })
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
    nickname: null,
    path: null,
    video: false,
    audio: true,
    hasVideo: false,
    speaker: true,
    mute: false,
    status: 'good',
    roleType: roleType,
    deviceType: deviceType,
    permission: 'default',
    hasArFeature: false,
    cameraStatus: 'default',
    zoomLevel: 1, // zoom 레벨
    zoomMax: 1, // zoom 최대 레벨
    flash: 'default', // flash 제어
  }
  const account = Store.getters['account']
  if (account.uuid === uuid) {
    userObj.nickname = account.nickname
    userObj.path = account.profile
    userObj.me = true
    Store.commit('addStream', userObj)
  } else {
    logger('room', "participant's connected")
    Store.commit('addStream', userObj)
    getUserInfo({
      userId: userObj.id,
    }).then(participant => {
      Store.commit('updateParticipant', {
        connectionId: event.connection.connectionId,
        nickname: participant.nickname,
        path: participant.profile,
      })
      const chatObj = {
        name: participant.nickname,
        status: 'invite',
        type: 'system',
      }
      Store.commit('addChat', chatObj)
    })
  }

  // return streamObj
}
