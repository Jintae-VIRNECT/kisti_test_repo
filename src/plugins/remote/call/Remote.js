import { OpenVidu } from './openvidu'
import { addSessionEventListener, getUserObject } from './RemoteUtils'
import { getToken } from 'api/workspace/call'
import Store from 'stores/remote/store'
import { SIGNAL, ROLE, DRAWING } from 'configs/remote.config'
import { allowCamera } from 'utils/testing'

let OV

const _ = {
  account: null,
  session: null,
  publisher: null,
  subscribers: [],
  resolution: null,
  join: async (roomInfo, account, role) => {
    Store.commit('clear')
    Store.dispatch('updateAccount', {
      roleType: role,
    })
    _.account = account
    // TODO: 영상 출력 허용 테스트 계정 이메일
    let allowUser = false
    if (allowCamera.includes(account.email)) {
      allowUser = true
    }
    try {
      const params = {
        sessionId: roomInfo.sessionId,
        role: 'PUBLISHER',
        data: {},
        kurentoOptions: {},
      }
      const rtnValue = await getToken(params)

      OV = new OpenVidu()
      _.session = OV.initSession()

      addSessionEventListener(_.session, Store)
      const metaData = {
        clientData: account.uuid,
        roleType: role,
      }

      const iceServers = [
        {
          url: 'turn:turn.virnectremote.com:3478?transport=udp',
          username: 'virnect',
          credential: 'virnect',
        },
        {
          url: 'turn:turn.virnectremote.com:3478?transport=tcp',
          username: 'virnect',
          credential: 'virnect',
        },
      ]

      await _.session.connect(
        rtnValue.token,
        JSON.stringify(metaData),
        iceServers,
      )
      const publishVideo = role === ROLE.WORKER || allowUser

      _.publisher = OV.initPublisher('', {
        audioSource: undefined, // TODO: setting value
        videoSource: undefined, //screen ? 'screen' : undefined,  // TODO: setting value
        publishAudio: true,
        publishVideo: publishVideo,
        resolution: '1280x720', // TODO: setting value
        frameRate: 30,
        insertMode: 'PREPEND',
        mirror: false,
      })
      _.publisher.on('streamCreated', () => {
        Store.commit('addStream', getUserObject(_.publisher.stream))
        _.mic(Store.getters['mic'])
        if (publishVideo) {
          Store.commit('updateResolution', {
            connectionId: _.publisher.stream.connection.connectionId,
            width: 0,
            height: 0,
          })
        }
      })

      _.session.publish(_.publisher)
      return true
    } catch (err) {
      console.error(err)
      return false
    }
  },
  leave: () => {
    try {
      _.session.disconnect()
      _.session = null
      _.publisher = null
    } catch (err) {
      throw err
    }
  },
  sendChat: text => {
    if (text.trim().length === 0) return
    _.session.signal({
      data: text.trim(),
      to: _.session.connection,
      type: SIGNAL.CHAT,
    })
  },
  sendResolution: resolution => {
    if (resolution) {
      _.resolution = resolution
    } else {
      resolution = _.resolution
    }
    if (!resolution || !resolution.width) return
    _.session.signal({
      data: JSON.stringify(resolution),
      to: _.session.connection,
      type: SIGNAL.RESOLUTION,
    })
  },
  shareImage: imgInfo => {
    const params = {
      imgId: imgInfo.id,
      from: _.account.uuid,
      width: imgInfo.width,
      height: imgInfo.height,
    }
    const chunkSize = 1024 * 10

    const chunk = []
    const base64 = imgInfo.img.replace(/data:image\/.+;base64,/, '')
    const chunkLength = Math.ceil(base64.length / chunkSize)
    let start = 0
    for (let i = 0; i < chunkLength; i++) {
      chunk.push(base64.substr(start, chunkSize))
      start += chunkSize
    }
    for (let i = 0; i < chunk.length; i++) {
      if (i === 0) params.type = DRAWING.FIRST_FRAME
      else if (i === chunk.length - 1) params.type = DRAWING.LAST_FRAME
      else params.type = DRAWING.FRAME
      params.chunk = chunk[i]
      _.session.signal({
        data: JSON.stringify(params),
        to: _.session.connection,
        type: SIGNAL.DRAWING,
      })
    }
  },
  drawing: (type, params) => {
    params.type = type
    params['from'] = _.account.uuid
    params['to'] = []
    _.session.signal({
      type: SIGNAL.DRAWING,
      to: _.session.connection,
      data: JSON.stringify(params),
    })
  },
  pointing: message => {
    _.session.signal({
      data: JSON.stringify(message),
      to: _.session.connection,
      type: SIGNAL.POINTING,
    })
  },
  arPointing: message => {
    _.session.signal({
      data: JSON.stringify(message),
      to: _.session.connection,
      type: SIGNAL.AR_POINTING,
    })
  },
  permission: (params = {}) => {
    params['from'] = _.account.uuid
    if (params.type !== 'response') params['type'] = 'request'
    _.session.signal({
      type: SIGNAL.CAPTURE_PERMISSION,
      to: _.session.connection,
      data: JSON.stringify(params),
    })
  },
  arDrawing: (type, params = {}) => {
    params.type = type
    params['from'] = _.account.uuid
    _.session.signal({
      type: SIGNAL.AR_DRAWING,
      to: _.session.connection,
      data: JSON.stringify(params),
    })
  },
  getState: () => {
    if (_.publisher) {
      return {
        audio: _.publisher.stream.audioActive,
        video: _.publisher.stream.videoActive,
      }
    } else {
      return {}
    }
  },
  streamOnOff: active => {
    _.publisher.publishVideo(active)
  },
  mic: active => {
    if (!_.publisher) return
    _.publisher.publishAudio(active)
    const params = {
      isOn: active,
    }
    _.session.signal({
      data: JSON.stringify(params),
      to: _.session.connection,
      type: SIGNAL.MIC,
    })
  },
  speaker: active => {
    for (let subscriber of _.subscribers) {
      subscriber.subscribeToAudio(active)
    }
    const params = {
      isOn: active,
    }
    _.session.signal({
      data: JSON.stringify(params),
      to: _.session.connection,
      type: SIGNAL.SPEAKER,
    })
  },
  mute: (connectionId, mute) => {
    let idx = _.subscribers.findIndex(
      subscriber => subscriber.stream.connection.connectionId === connectionId,
    )
    if (idx < 0) {
      console.log('can not find user')
      return
    }
    _.subscribers[idx].subscribeToAudio(!mute)
    Store.commit('propertyChanged', {
      connectionId: connectionId,
      mute: mute,
    })
  },
  disconnect: connectionId => {
    let idx = _.subscribers.findIndex(
      subscriber => subscriber.stream.connection.connectionId === connectionId,
    )
    if (idx < 0) {
      console.log('can not find user')
      return
    }
    _.session.forceDisconnect(_.subscribers[idx].stream.connection)
  },
  /**
   * append message channel listener
   * @param {Function} customFunc
   */
  addListener: (type, func) => {
    _.session.on(type, func)
  },
  removeListener: (type, func) => {
    // _.session.off(type, func)
  },
}

export const addSubscriber = subscriber => {
  console.log(subscriber)
  _.subscribers.push(subscriber)
}

export const removeSubscriber = streamId => {
  const idx = _.subscribers.findIndex(user => user.stream.streamId === streamId)
  if (idx < 0) return
  _.subscribers.splice(idx, 1)
}

export default _
