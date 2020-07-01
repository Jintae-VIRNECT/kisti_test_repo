import { OpenVidu } from './openvidu'
import { addSessionEventListener, getUserObject } from './RemoteUtils'
import { getToken } from 'api/workspace/call'
import Store from 'stores/remote/store'
import { SIGNAL, ROLE, CAMERA, FLASH } from 'configs/remote.config'
import { allowCamera } from 'utils/testing'

let OV

const _ = {
  account: null,
  session: null,
  publisher: null,
  subscribers: [],
  // 필요여부 체크할 것
  resolution: null,
  /**
   * join session
   * @param {Object}
   * @param {Object}
   * @param {String}
   */
  join: async (roomInfo, role) => {
    // role = ROLE.EXPERT
    Store.commit('clear')
    Store.dispatch('updateAccount', {
      roleType: role,
    })
    _.account = Store.getters['account']
    // TODO: 영상 출력 허용 테스트 계정 이메일
    let allowUser = false
    if (allowCamera.includes(_.account.email)) {
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
        clientData: _.account.uuid,
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

      const publisher = OV.initPublisher('', {
        audioSource: undefined, // TODO: setting value
        videoSource: undefined, //screen ? 'screen' : undefined,  // TODO: setting value
        publishAudio: true,
        publishVideo: publishVideo,
        resolution: '1280x720', // TODO: setting value
        frameRate: 30,
        insertMode: 'PREPEND',
        mirror: false,
      })
      publisher.on('streamCreated', () => {
        _.publisher = publisher
        Store.commit('addStream', getUserObject(publisher.stream))
        _.mic(Store.getters['mic'].isOn)
        if (publishVideo) {
          Store.commit('updateResolution', {
            connectionId: publisher.stream.connection.connectionId,
            width: 0,
            height: 0,
          })
        }
      })

      _.session.publish(publisher)
      return true
    } catch (err) {
      console.error(err)
      return false
    }
  },
  /**
   * leave session
   */
  leave: () => {
    try {
      _.session.disconnect()
      _.session = null
      _.publisher = null
    } catch (err) {
      throw err
    }
  },
  /**
   * chatting
   * @param {String} text
   */
  sendChat: text => {
    if (!_.session) return
    if (text.trim().length === 0) return
    _.session.signal({
      data: text.trim(),
      to: _.session.connection,
      type: SIGNAL.CHAT,
    })
  },
  /**
   * resolution
   * @param {Object} resolution = {width, height, orientation}
   */
  sendResolution: resolution => {
    if (!_.session) return
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
  /**
   * pointing
   * @param {Object} params
   *  = {to, from, color, opacity, width, posX, posY}
   */
  pointing: params => {
    if (!_.session) return
    _.session.signal({
      data: JSON.stringify(params),
      to: _.session.connection,
      type: SIGNAL.POINTING,
    })
  },
  /**
   * sharing drawing
   * @param {String} type = remote.config.DRAWING
   * @param {Object} params
   */
  drawing: (type, params = {}) => {
    params.type = type
    params['from'] = _.account.uuid
    params['to'] = []
    _.session.signal({
      type: SIGNAL.DRAWING,
      to: _.session.connection,
      data: JSON.stringify(params),
    })
  },
  /**
   * other user's pointing, recording control
   * @param {String} type = remote.config.CONTROL
   */
  control: (type, enable) => {
    const params = {
      type,
      enable,
    }
    _.session.signal({
      data: JSON.stringify(params),
      to: _.session.connection,
      type: SIGNAL.CONTROL,
    })
  },
  /**
   * AR feature status
   * @param {String} type = remote.config.AR_FEATURE
   */
  arFeature: type => {
    const params = {
      type: type,
    }
    _.session.signal({
      data: JSON.stringify(params),
      to: _.session.connection,
      type: SIGNAL.AR_FEATURE,
    })
  },
  /**
   * AR pointing
   * @param {String} type = remote.config.AR_POINTING
   * @param {Object} params (문서참조)
   */
  arPointing: (type, params = {}) => {
    params.type = type
    _.session.signal({
      data: JSON.stringify(params),
      to: _.session.connection,
      type: SIGNAL.AR_POINTING,
    })
  },
  /**
   * request screen capture permission
   * @param {Object} params
   */
  permission: (params = {}) => {
    params['from'] = _.account.uuid
    if (params.type !== 'response') params['type'] = 'request'
    _.session.signal({
      type: SIGNAL.CAPTURE_PERMISSION,
      to: _.session.connection,
      data: JSON.stringify(params),
    })
  },
  /**
   * AR drawing
   * @param {String} type = remote.config.AR_DRAWING
   * @param {Object} params (문서참조)
   */
  arDrawing: (type, params = {}) => {
    params.type = type
    params['from'] = _.account.uuid
    _.session.signal({
      type: SIGNAL.AR_DRAWING,
      to: _.session.connection,
      data: JSON.stringify(params),
    })
  },
  /**
   * @WORNNING no used
   * my video stream control
   */
  streamOnOff: active => {
    _.publisher.publishVideo(active)
  },
  /**
   * my mic control
   * @param {Boolean} active
   */
  mic: active => {
    if (!_.publisher) return
    _.publisher.publishAudio(active)
    const params = {
      isOn: active,
    }
    try {
      _.session.signal({
        data: JSON.stringify(params),
        to: _.session.connection,
        type: SIGNAL.MIC,
      })
    } catch (err) {
      return false
    }
  },
  /**
   * my speaker control
   * @param {Boolean} active
   */
  speaker: active => {
    for (let subscriber of _.subscribers) {
      subscriber.subscribeToAudio(active)
    }
    const params = {
      isOn: active,
    }
    try {
      _.session.signal({
        data: JSON.stringify(params),
        to: _.session.connection,
        type: SIGNAL.SPEAKER,
      })
    } catch (err) {
      return false
    }
  },
  /**
   * other user's flash control
   * @param {Boolean} active
   */
  flash: active => {
    const params = {
      enable: active,
      from: _.account.uuid,
      type: FLASH.FLASH,
    }
    _.session.signal({
      data: JSON.stringify(params),
      to: _.session.connection,
      type: SIGNAL.FLASH,
    })
  },
  /**
   * other user's camera control
   * @param {Boolean} active
   */
  zoom: level => {
    const params = {
      from: _.account.uuid,
      type: CAMERA.ZOOM,
      level: level,
    }
    _.session.signal({
      data: JSON.stringify(params),
      to: _.session.connection,
      type: SIGNAL.CAMERA,
    })
  },
  /**
   * user's speaker mute
   * @param {String} connectionId
   * @param {Boolean} mute
   */
  mute: (connectionId, mute) => {
    let idx = _.subscribers.findIndex(
      subscriber => subscriber.stream.connection.connectionId === connectionId,
    )
    if (idx < 0) {
      console.log('can not find user')
      return
    }
    _.subscribers[idx].subscribeToAudio(!mute)
    // TODO: 이건머냐!!!!
    Store.commit('updateParticipant', {
      connectionId: connectionId,
      mute: mute,
    })
  },
  /**
   * kickout user
   * @param {String} connectionId
   */
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
  /**
   * append session signal listener
   * @param {String} type = remote.config.SIGNAL
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
