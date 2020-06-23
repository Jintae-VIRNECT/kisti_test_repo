import Store from 'stores/remote/store'
import _, { addSubscriber, removeSubscriber } from './Remote'
import { SIGNAL } from 'configs/remote.config'

export const addSessionEventListener = session => {
  session.on('streamCreated', event => {
    const subscriber = session.subscribe(event.stream, '', () => {
      const streamObj = getUserObject(subscriber.stream)
      Store.commit('addStream', streamObj)
      _.sendResolution()
    })
    addSubscriber(subscriber)
  })
  session.on('streamPropertyChanged', event => {
    if (event.changedProperty === 'audioActive') {
      // audio 조절 :::: SIGNAL.MIC로 대체
      // Store.commit('updateParticipant', {
      //   connectionId: event.stream.connection.connectionId,
      //   audio: event.newValue,
      // })
    } else if (event.changedProperty === 'videoActive') {
      Store.commit('updateParticipant', {
        connectionId: event.stream.connection.connectionId,
        video: event.newValue,
      })
    }
  })

  session.on('streamDestroyed', event => {
    console.log('[session] stream destroyed')
    const connectionId = event.stream.connection.connectionId
    Store.commit('removeStream', connectionId)
    removeSubscriber(event.stream.streamId)
  })

  /** 상대방 마이크 활성 정보 수신 */
  session.on(SIGNAL.MIC, event => {
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
  /**AR 사용 가능 여부 >> AR_FEATURE랑 합쳐져야함 */
  // session.on(SIGNAL.HAS_AR_FEATURE, event => {
  //   if (session.connection.connectionId === event.from.connectionId) return
  //   const data = JSON.parse(event.data)
  //   Store.commit('updateParticipant', {
  //     connectionId: event.from.connectionId,
  //     arFeature: data.hasArFeature,
  //   })
  // })
  session.on(SIGNAL.AR_FEATURE, event => {
    if (session.connection.connectionId === event.from.connectionId) return
    const data = JSON.parse(event.data)
    Store.commit('updateParticipant', {
      connectionId: event.from.connectionId,
      arFeature: data.hasArFeature,
    })
  })
  /** 플래시 컨트롤 */
  session.on(SIGNAL.FLASH, event => {
    if (session.connection.connectionId === event.from.connectionId) return
    const data = JSON.parse(event.data)
    Store.commit('deviceControl', {
      flash: data.status,
    })
  })
  /** 카메라 컨트롤(zoom) */
  session.on(SIGNAL.CAMERA, event => {
    if (session.connection.connectionId === event.from.connectionId) return
    const data = JSON.parse(event.data)
    Store.commit('deviceControl', {
      zoom: data.currentZoomLevel,
      zoomMax: data.maxZoomLevel,
      cameraStatus: data.status,
    })
  })
  /** 화면 해상도 설정 */
  session.on(SIGNAL.RESOLUTION, event => {
    Store.commit('updateResolution', {
      ...JSON.parse(event.data),
      connectionId: event.from.connectionId,
    })
  })
  // session.on(SIGNAL.CAPTURE_PERMISSION, event => {
  //   const data = JSON.parse(event.data)
  //   if (data.type === 'response') {
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
    let chat = {
      text: data.replace(/\</g, '&lt;'),
      name: participants[idx].nickname,
      date: new Date(),
      nodeId: event.from.connectionId,
      type: false,
    }
    if (session.connection.connectionId === event.from.connectionId) {
      // 본인
      chat.type = 'me'
    }
    Store.commit('addChat', chat)
  })
  /** 내보내기 */
  session.on('forceDisconnectByUser', event => {
    console.log(event)
  })
}

export const getUserObject = stream => {
  console.log(stream)
  const participants = Store.getters['roomParticipants']
  let streamObj
  let connection = stream.connection

  const metaData = JSON.parse(connection.data.split('%/%')[0])

  let uuid = metaData.clientData
  let roleType = metaData.roleType

  const participant = participants.find(user => {
    return user.uuid === uuid
  })
  if (participant === undefined) {
    console.error('참여자 정보를 찾을 수 없습니다.')
    return
  }

  streamObj = {
    id: uuid,
    stream: stream.mediaStream,
    // connection: stream.connection,
    connectionId: stream.connection.connectionId,
    nickname: participant.nickname,
    path: participant.path,
    audio: stream.audioActive,
    video: stream.videoActive,
    speaker: true,
    mute: false,
    status: 'good',
    roleType: roleType,
    permission: 'default',
    hasArFeature: 'default',
  }
  if (stream.videoActive) {
    // Store.commit('updateResolution', {
    //   connectionId: stream.connection.connectionId,
    //   width: 0,
    //   height: 0,
    // })
  }
  if (Store.getters['account'].uuid === uuid) {
    streamObj.me = true
  }

  return streamObj
}

export const getStream = async constraints => {
  if (!navigator.mediaDevices.getUserMedia) {
    console.error('getUserMedia 없음')
    return
  }
  try {
    const mediaStream = await navigator.mediaDevices.getUserMedia(constraints)

    const streamTracks = {}
    streamTracks.audioSource =
      mediaStream.getAudioTracks().length > 0
        ? mediaStream.getAudioTracks()[0]
        : null
    streamTracks.videoSource =
      mediaStream.getVideoTracks().length > 0
        ? mediaStream.getVideoTracks()[0]
        : null
    return streamTracks
  } catch (err) {
    console.error(err)
  }
}
