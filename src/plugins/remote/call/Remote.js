import { OpenVidu } from './openvidu'
import { addSessionEventListener } from './RemoteUtils'
import Store from 'stores/remote/store'
import { SIGNAL, ROLE, CAMERA, FLASH } from 'configs/remote.config'
import { DEVICE } from 'configs/device.config'
import { allowCamera } from 'utils/testing'
import { logger, debug } from 'utils/logger'
import { wsUri } from 'api/gateway/api'

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
   * @param {Object} configs {coturn, wss, token}
   * @param {String} role remote.config.ROLE
   */
  connect: async (configs, role) => {
    _.account = Store.getters['account']
    const settingInfo = Store.getters['settingInfo']
    try {
      Store.commit('callClear')
      OV = new OpenVidu()
      _.session = OV.initSession()

      addSessionEventListener(_.session, Store)
      const metaData = {
        clientData: _.account.uuid,
        roleType: role,
        deviceType: DEVICE.WEB,
      }

      const iceServers = configs.coturn || window.urls.coturn
      // const iceServers = window.urls.coturn
      const ws = configs.wss || `${window.urls.wsapi}${wsUri['REMOTE']}`
      // const ws = 'wss://192.168.6.3:8000/remote/websocket'

      if (!iceServers) {
        throw 'ice server를 찾을 수 없습니다.'
      }
      debug('coturn::', iceServers)

      const options = {
        iceServers,
        wsUri: ws,
        role: 'PUSLISHER',
      }

      await _.session.connect(configs.token, JSON.stringify(metaData), options)

      Store.dispatch('updateAccount', {
        roleType: role,
      })
      const publishVideo = role === ROLE.WORKER || role === ROLE.EXPERT || true

      const publisher = OV.initPublisher('', {
        audioSource: settingInfo.mic ? settingInfo.mic : undefined, // TODO: setting value
        videoSource: settingInfo.video ? settingInfo.video : undefined, //screen ? 'screen' : undefined,  // TODO: setting value
        publishAudio: true,
        publishVideo: publishVideo,
        resolution: '1280x720', // TODO: setting value
        frameRate: 30,
        insertMode: 'PREPEND',
        mirror: false,
      })
      publisher.on('streamCreated', () => {
        logger('room', 'publish success')
        _.publisher = publisher
        const mediaStream = publisher.stream.mediaStream
        const streamSize = mediaStream.getVideoTracks()[0].getSettings()
        Store.commit('updateParticipant', {
          connectionId: publisher.stream.connection.connectionId,
          stream: mediaStream,
        })
        _.sendResolution({
          width: streamSize.width,
          height: streamSize.height,
          orientation: '',
        })
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
      if (!_.session) return
      _.session.disconnect()
      _.account = null
      _.session = null
      _.publisher = null
      _.subscribers = []
      _.resolution = null
      // 필요여부 체크할 것
    } catch (err) {
      throw err
    }
  },
  /**
   * leave session
   */
  clear: () => {
    _.account = null
    _.session = null
    _.publisher = null
    _.subscribers = []
    _.resolution = null
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
   * chatting-file
   */
  sendFile: params => {
    if (!_.session) return

    //파일 관련 정보 전송하기
    _.session.signal({
      data: JSON.stringify(params),
      to: _.session.connection,
      type: SIGNAL.FILE,
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

      Store.commit('updateResolution', {
        connectionId: _.session.connection.connectionId,
        width: resolution.width,
        height: resolution.height,
      })
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
    if (!_.session) return
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
    if (_.publisher) {
      _.publisher.publishAudio(active)
    }
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
  // console.log(subscriber)
  _.subscribers.push(subscriber)
}

export const removeSubscriber = streamId => {
  const idx = _.subscribers.findIndex(user => user.stream.streamId === streamId)
  if (idx < 0) return
  _.subscribers.splice(idx, 1)
}

export default _
