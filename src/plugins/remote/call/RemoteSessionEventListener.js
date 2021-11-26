import Store from 'stores/remote/store'
import _, { addSubscriber, removeSubscriber } from './Remote'

import { URLS } from 'configs/env.config'

import auth from 'utils/auth'

import {
  SIGNAL,
  CONTROL,
  CAMERA,
  FLASH,
  ROLE,
  VIDEO,
  FILE,
  DRAWING,
} from 'configs/remote.config'
import { CAMERA as CAMERA_STATUS } from 'configs/device.config'

import { logger, debug } from 'utils/logger'

//DRAWING FILE SHARE시그날 이벤트 vuex에 큐로 저장 활성화 여부
export let queueAvtivated = true
export const setQueueAct = isActiv => {
  queueAvtivated = isActiv
}

const streamCreated = event => {
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
  const subscriber = _.session.subscribe(event.stream, '', () => {
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
}

const streamDestroyed = event => {
  logger('room', 'stream destroy', event.reason)
  if (event.reason === 'streamNotExist') {
    const connectionId = event.stream.connection.connectionId
    Store.commit('removeStream', connectionId)
    _.removeSubscriber(connectionId)
  }
}

/** session closed */
const sessionDisconnected = async event => {
  const isGuest = _.account.roleType === ROLE.GUEST
  if (event.reason === 'sessionClosedByServer') {
    logger('room', 'participant disconnect')
    _.clear()
    window.vue.$toasted.show(
      window.vue.$t('workspace.confirm_removed_room_leader'),
      {
        position: 'bottom-center',
        className: 'remote-toast',
        duration: 5000,
        action: {
          icon: 'close',
          onClick: (e, toastObject) => {
            toastObject.goAway(0)
          },
        },
      },
    )

    if (isGuest) {
      await auth.logout(false)
      location.href = `${URLS['console']}`
    } else {
      window.vue.$router.push({ name: 'workspace' })
    }
  } else if (event.reason === 'forceDisconnectByUser') {
    logger('room', 'participant disconnect')
    _.clear()
    window.vue.$toasted.show(
      window.vue.$t('workspace.confirm_kickout_leader'),
      {
        position: 'bottom-center',
        className: 'remote-toast',
        duration: 5000,
        action: {
          icon: 'close',
          onClick: (e, toastObject) => {
            toastObject.goAway(0)
          },
        },
      },
    )

    if (isGuest) {
      await auth.logout(false)
      location.href = `${URLS['console']}`
    } else {
      window.vue.$router.push({ name: 'workspace' })
    }
  } else if (event.reason === 'networkDisconnect') {
    logger('network', 'disconnect')
  }
}

// user leave
const connectionDestroyed = event => {
  logger('room', 'participant destroy')
  const connectionId = event.connection.connectionId
  Store.commit('removeStream', connectionId)
  removeSubscriber(event.connection.connectionId)
}

/** 메인뷰 변경 */
const signalVideo = event => {
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
    const noStream = [disabled, isSame, noCamera].every(condition => condition)
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
      if (data.type === VIDEO.NORMAL) {
        Store.commit('updateParticipant', {
          connectionId: event.from.connectionId,
          currentWatching: data.id,
        })

        //리더가 VIDEO.NORMAL 시그널을 보냈을 때만 전체화면 공유를 풀어야함.
        const participants = Store.getters['participants']
        const idx = participants.findIndex(
          user =>
            user.roleType === ROLE.LEADER &&
            user.connectionId === event.from.connectionId,
        )

        if (idx >= 0) {
          Store.dispatch('setMainView', { force: false })
        }
      }
    }
  }
}

/** 상대방 마이크 활성 정보 수신 */
const signalMic = event => {
  window.vue.$eventBus.$emit(SIGNAL.MIC, event)
  if (_.session.connection.connectionId === event.from.connectionId) return
  const data = JSON.parse(event.data)
  Store.commit('updateParticipant', {
    connectionId: event.from.connectionId,
    audio: data.isOn,
  })
}

/** 상대방 스피커 활성 정보 수신 */
const signalSpeaker = event => {
  window.vue.$eventBus.$emit(SIGNAL.SPEAKER, event)
  if (_.session.connection.connectionId === event.from.connectionId) return
  const data = JSON.parse(event.data)
  Store.commit('updateParticipant', {
    connectionId: event.from.connectionId,
    speaker: data.isOn,
  })
}

/** 플래시 컨트롤 */
const signalFlash = event => {
  window.vue.$eventBus.$emit(SIGNAL.FLASH, event)
  // if (session.connection.connectionId === event.from.connectionId) return
  const data = JSON.parse(event.data)
  if (data.type !== FLASH.STATUS) return
  Store.commit('deviceControl', {
    connectionId: event.from.connectionId,
    flash: data.status,
  })
}

/** 카메라 컨트롤(zoom) */
const signalCamera = event => {
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
    if (_.session.connection.connectionId === event.from.connectionId) {
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
}

/** 화면 해상도 설정 */
const signalResolution = event => {
  window.vue.$eventBus.$emit(SIGNAL.RESOLUTION, event)
  if (_.session.connection.connectionId === event.from.connectionId) return
  Store.commit('updateResolution', {
    ...JSON.parse(event.data),
    connectionId: event.from.connectionId,
  })
}

/** 리더 컨트롤(pointing, local record) */
const signalControl = event => {
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
}

/** 채팅 수신 */
const signalChat = event => {
  window.vue.$eventBus.$emit(SIGNAL.CHAT, event)
  const connectionId = event.from.connectionId
  const participants = Store.getters['participants']
  const idx = participants.findIndex(user => user.connectionId === connectionId)
  if (idx < 0) return
  let data = JSON.parse(event.data)
  Store.commit('addChat', {
    type:
      _.session.connection.connectionId === event.from.connectionId
        ? 'me'
        : 'opponent',
    name: participants[idx].nickname,
    profile: participants[idx].path,
    mute: participants[idx].mute,
    connectionId: event.from.connectionId,
    text: data.text,
    languageCode: data.languageCode,
  })
}

/** 채팅 파일 수신 */
const signalChatFile = event => {
  window.vue.$eventBus.$emit(SIGNAL.FILE, event)
  const connectionId = event.from.connectionId
  const participants = Store.getters['participants']
  const idx = participants.findIndex(user => user.connectionId === connectionId)
  if (idx < 0) return
  let data = JSON.parse(event.data)
  if (data.type === FILE.UPLOADED) {
    Store.commit('addChat', {
      type:
        _.session.connection.connectionId === event.from.connectionId
          ? 'me'
          : 'opponent',
      name: participants[idx].nickname,
      profile: participants[idx].path,
      uuid: event.from.connectionId,
      file: data.fileInfo,
    })
  }
}

/** Pointing */
const signalPointing = event => {
  window.vue.$eventBus.$emit(SIGNAL.POINTING, event)
}

/** Drawing */
const signalDrawing = event => {
  const data = JSON.parse(event.data)
  //큐가 활성화된 상태고, FILE SHARE 이벤트인 경우 Vuex에 저장해둔다.
  //해당 이벤트를 필요로하는 component가 활성화 되었을 경우 해당 vuex내 값을 큐처럼 순차적으로 처리한다.
  if (queueAvtivated && data.type === DRAWING.FILE_SHARE) {
    Store.commit('addFileShareEvent', { data, receive: event })
  }
  //큐가 활성화되지 않은 경우와 다른 DRAWING 타입의 경우 일반적으로 이벤트로 전달한다
  else window.vue.$eventBus.$emit(SIGNAL.DRAWING, { data, receive: event })
}

/** screen capture permission 수신 */
const signalCapturePermission = event => {
  window.vue.$eventBus.$emit(SIGNAL.CAPTURE_PERMISSION, event)
}

/** AR feature */
const signalArFeature = event => {
  window.vue.$eventBus.$emit(SIGNAL.AR_FEATURE, event)
}

/** AR Pointing */
const signalArPointing = event => {
  window.vue.$eventBus.$emit(SIGNAL.AR_POINTING, event)
}

/** AR Drawing */
const signalArDrawing = event => {
  window.vue.$eventBus.$emit(SIGNAL.AR_DRAWING, event)
}

/** AR 3D 콘텐츠 모드*/
const signalAr3dContent = event => {
  window.vue.$eventBus.$emit(SIGNAL.AR_3D, event)
}

export default {
  streamCreated,
  streamDestroyed,
  sessionDisconnected,
  connectionDestroyed,
  signalVideo,
  signalMic,
  signalSpeaker,
  signalFlash,
  signalCamera,
  signalResolution,
  signalControl,
  signalChat,
  signalChatFile,
  signalPointing,
  signalDrawing,
  signalCapturePermission,
  signalArFeature,
  signalArPointing,
  signalArDrawing,
  signalAr3dContent,
}
