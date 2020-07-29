import Store from 'stores/remote/store'
import _, { addSubscriber, removeSubscriber } from './Remote'

import { SIGNAL, CONTROL, CAMERA, FLASH, ROLE } from 'configs/remote.config'
import { TYPE } from 'configs/chat.config'

import ChatMsgBuilder from 'utils/chatMsgBuilder'
import { allowCamera } from 'utils/testing'
import { getUserInfo } from 'api/common'
import vue from 'apps/remote/app'
import { logger } from 'utils/logger'

export const addSessionEventListener = session => {
  session.on('connectionCreated', event => {
    logger('room', 'connection created')
    setUserObject(event)
  })
  session.on('streamCreated', event => {
    const subscriber = session.subscribe(event.stream, '', () => {
      logger('room', 'participant subscribe successed')
      Store.commit('updateParticipant', {
        connectionId: event.stream.connection.connectionId,
        stream: event.stream.mediaStream,
      })
      _.sendResolution()
      _.mic(Store.getters['mic'].isOn)
      _.speaker(Store.getters['speaker'].isOn)
      if (_.account.roleType === ROLE.EXPERT_LEADER) {
        _.control(CONTROL.POINTING, Store.getters['allowPointing'])
        _.control(CONTROL.LOCAL_RECORD, Store.getters['allowLocalRecord'])
      }
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
  /** session closed */
  session.on('sessionDisconnected', event => {
    logger('room', 'participant disconnect')
    _.clear()
    if (event.reason === 'sessionClosedByServer') {
      // TODO: MESSAGE
      vue.$toasted.error('리더가 협업을 삭제했습니다.', {
        position: 'bottom-center',
        duration: 5000,
        action: {
          icon: 'close',
          onClick: (e, toastObject) => {
            toastObject.goAway(0)
          },
        },
      })
      vue.$router.push({ name: 'workspace' })
    }
  })
  // user leave
  session.on('streamDestroyed', event => {
    logger('room', 'participant destroy')
    const connectionId = event.stream.connection.connectionId
    Store.commit('removeStream', connectionId)
    removeSubscriber(event.stream.streamId)
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
    if (session.connection.connectionId === event.from.connectionId) return
    const data = JSON.parse(event.data)
    if (data.type !== FLASH.STATUS) return
    Store.commit('deviceControl', {
      flash: data.status,
    })
  })
  /** 카메라 컨트롤(zoom) */
  session.on(SIGNAL.CAMERA, event => {
    if (session.connection.connectionId === event.from.connectionId) return
    const data = JSON.parse(event.data)
    if (data.type !== CAMERA.STATUS) return
    Store.commit('deviceControl', {
      zoomLevel: parseFloat(data.currentZoomLevel),
      zoomMax: parseInt(data.maxZoomLevel),
      cameraStatus: parseInt(data.status),
    })
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
    if (session.connection.connectionId === event.from.connectionId) return
    const data = JSON.parse(event.data)
    if (data.type === CONTROL.POINTING) {
      Store.commit('deviceControl', {
        allowPointing: data.enable,
      })
    } else if (data.type === CONTROL.LOCAL_RECORD) {
      Store.commit('deviceControl', {
        allowLocalRecord: data.enable,
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

    const chatBuilder = new ChatMsgBuilder()
      .setName(participants[idx].nickname)
      .setText(data)
      .setType(TYPE.OPPONENT)
      .setId(event.from.connectionId)
      .setProfileImg(participants[idx].path ? participants[idx].path : null)

    if (session.connection.connectionId === event.from.connectionId) {
      // 본인
      chatBuilder.setType(TYPE.ME)
    }

    Store.commit('addChat', chatBuilder.build())
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

    const chatBuilder = new ChatMsgBuilder()
      .setType(TYPE.OPPONENT)
      .setName(participants[idx].nickname)
      .setId(event.from.connectionId)
      .setProfileImg(participants[idx].path)
      .setFile([
        {
          fileName: data.fileName,
          fileSize: data.size,
          fileUrl: data.fileDownloadUrl,
        },
      ])

    if (session.connection.connectionId === event.from.connectionId) {
      // 본인
      chatBuilder.setType(TYPE.ME)
    }

    Store.commit('addChat', chatBuilder.build())
  })

  /** 내보내기 */
  session.on('forceDisconnectByUser', event => {
    // console.log(event)
  })
}

const setUserObject = event => {
  let userObj
  let connection = event.connection

  const metaData = JSON.parse(connection.data.split('%/%')[0])

  let uuid = metaData.clientData
  let roleType = metaData.roleType
  let deviceType = metaData.deviceType

  let allowUser = false
  if (allowCamera.includes(_.account.email)) {
    allowUser = true
  }

  userObj = {
    id: uuid,
    // stream: stream.mediaStream,
    stream: null,
    connectionId: connection.connectionId,
    // nickname: participant.nickname,
    // path: participant.profile,
    nickname: null,
    path: null,
    video: roleType === ROLE.WORKER || allowUser,
    audio: true,
    speaker: true,
    mute: false,
    status: 'good',
    roleType: roleType,
    deviceType: deviceType,
    permission: 'default',
    hasArFeature: false,
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
        text: participant.nickname + '님이 대화에 참여하셨습니다.',
        name: 'people',
        date: new Date(),
        uuid: null,
        type: 'system',
      }
      Store.commit('addChat', chatObj)
    })
  }

  // return streamObj
}
