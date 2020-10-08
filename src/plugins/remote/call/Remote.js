import { OpenVidu } from './openvidu'
import { addSessionEventListener } from './RemoteUtils'
import Store from 'stores/remote/store'
import {
  SIGNAL,
  ROLE,
  CAMERA,
  FLASH,
  VIDEO,
  AR_FEATURE,
} from 'configs/remote.config'
import {
  DEVICE,
  FLASH as FLASH_STATUE,
  CAMERA as CAMERA_STATUE,
} from 'configs/device.config'
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
  currentZoomLevel: 1,
  maxZoomLevel: 1,
  openRoom: false,
  /**
   * join session
   * @param {Object} configs {coturn, wss, token}
   * @param {String} role remote.config.ROLE
   */
  connect: async (configs, role, options) => {
    try {
      _.account = Store.getters['account']
      _.openRoom = Store.getters['openRoom']

      Store.commit('callClear')
      OV = new OpenVidu()
      if (process.env.NODE_ENV === 'production') {
        OV.enableProdMode()
      }
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

      const connectOption = {
        iceServers,
        wsUri: ws,
        role: 'PUSLISHER',
      }

      await _.session.connect(
        configs.token,
        JSON.stringify(metaData),
        connectOption,
      )

      Store.dispatch('updateAccount', {
        roleType: role,
      })
      _.account.roleType = role
      const settingInfo = Store.getters['settingInfo']

      const publishOptions = {
        audioSource: options.audioSource,
        videoSource: options.videoSource,
        publishAudio: settingInfo.micOn,
        publishVideo: settingInfo.videoOn,
        resolution: settingInfo.quality,
        // resolution: '1920x1080', // FHD
        // resolution: '3840x2160', // 4K
        frameRate: 30,
        insertMode: 'PREPEND',
        mirror: false,
      }
      debug('call::publish::', publishOptions)

      _.publisher = OV.initPublisher('', publishOptions)
      _.publisher.onIceStateChanged(state => {
        if (
          state === 'failed' ||
          state === 'disconnected' ||
          state === 'closed'
        ) {
          Store.commit('updateParticipant', {
            connectionId: _.publisher.stream.connection.connectionId,
            status: 'bad',
          })
        } else if (state === 'connected') {
          Store.commit('updateParticipant', {
            connectionId: _.publisher.stream.connection.connectionId,
            status: 'good',
          })
        } else {
          Store.commit('updateParticipant', {
            connectionId: _.publisher.stream.connection.connectionId,
            status: 'normal',
          })
        }
        logger('ice state change', state)
      })
      _.publisher.on('streamCreated', () => {
        logger('room', 'publish success')
        const mediaStream = _.publisher.stream.mediaStream
        Store.commit('updateParticipant', {
          connectionId: _.publisher.stream.connection.connectionId,
          stream: mediaStream,
          hasVideo: _.publisher.stream.hasVideo,
          video: _.publisher.stream.videoActive,
          audio: _.publisher.stream.audioActive,
        })
        if (_.publisher.stream.hasVideo) {
          const track = mediaStream.getVideoTracks()[0]
          const settings = track.getSettings()
          const capability = track.getCapabilities()
          logger('call', `resolution::${settings.width}X${settings.height}`)
          debug('call::setting::', settings)
          debug('call::capability::', capability)
          if ('zoom' in capability) {
            track.applyConstraints({
              advanced: [{ zoom: capability['zoom'].min }],
            })
            _.maxZoomLevel = parseInt(capability.zoom.max / capability.zoom.min)
            _.minZoomLevel = parseInt(capability.zoom.min)
          }
          _.video(_.publisher.stream.videoActive)
          _.sendResolution({
            width: settings.width,
            height: settings.height,
            orientation: '',
          })
        }
      })

      _.session.publish(_.publisher)
      return true
    } catch (err) {
      if (err && err.message && err.message.length > 0) {
        console.error(`${err.message} (${err.code})`)
      } else {
        console.error(err)
      }
      throw err
    }
  },
  /**
   * @BROADCATE
   * chatting
   * @param {String} text
   */
  sendChat: (text, code = 'ko') => {
    if (!_.session) return
    if (text.trim().length === 0) return
    const params = {
      text: text.trim(),
      languageCode: code,
    }
    _.session.signal({
      data: JSON.stringify(params),
      to: null,
      type: SIGNAL.CHAT,
    })
  },

  /**
   * @BROADCATE
   * chatting-file
   */
  sendFile: params => {
    if (!_.session) return

    //파일 관련 정보 전송하기
    _.session.signal({
      data: JSON.stringify(params),
      to: null,
      type: SIGNAL.FILE,
    })
  },

  /**
   * @TARGET
   * resolution
   * @param {Object} resolution = {width, height, orientation}
   */
  sendResolution: (resolution, target = null) => {
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
      to: target,
      type: SIGNAL.RESOLUTION,
    })
  },
  /**
   * @BROADCATE
   * main view change (only leader)
   * @param {String} uuid
   * @param {Boolean} force true / false
   */
  mainview: (uuid, force = false, target = null) => {
    if (_.account.roleType !== ROLE.LEADER) return
    if (!uuid) uuid = _.account.uuid
    const params = {
      id: uuid,
      type: force ? VIDEO.SHARE : VIDEO.NORMAL,
    }
    _.session.signal({
      data: JSON.stringify(params),
      to: target,
      type: SIGNAL.VIDEO,
    })
  },
  /**
   * @BROADCATE
   * pointing
   * @param {Object} params
   *  = {color, opacity, width, posX, posY}
   */
  pointing: params => {
    if (!_.session) return
    _.session.signal({
      data: JSON.stringify(params),
      to: null,
      type: SIGNAL.POINTING,
    })
  },
  /**
   * @BROADCATE
   * sharing drawing
   * @param {String} type = remote.config.DRAWING
   * @param {Object} params
   */
  drawing: (type, params = {}, target = null) => {
    params.type = type
    _.session.signal({
      type: SIGNAL.DRAWING,
      to: target,
      data: JSON.stringify(params),
    })
  },
  /**
   * @BROADCATE
   * @TARGET
   * other user's pointing, recording control
   * @param {String} type = remote.config.CONTROL
   */
  control: (type, enable, target = null) => {
    const params = {
      type,
      enable,
    }
    _.session.signal({
      data: JSON.stringify(params),
      to: target,
      type: SIGNAL.CONTROL,
    })
  },
  /**
   * @BROADCATE
   * AR feature status
   * @param {String} type = remote.config.AR_FEATURE
   */
  startArFeature: targetId => {
    const params = {
      type: AR_FEATURE.START_AR_FEATURE,
      targetUserId: targetId,
    }
    _.session.signal({
      data: JSON.stringify(params),
      to: null,
      type: SIGNAL.AR_FEATURE,
    })
  },
  /**
   * @BROADCATE
   * AR feature status
   * @param {String} type = remote.config.AR_FEATURE
   */
  stopArFeature: () => {
    const params = {
      type: AR_FEATURE.STOP_AR_FEATURE,
    }
    _.session.signal({
      data: JSON.stringify(params),
      to: null,
      type: SIGNAL.AR_FEATURE,
    })
  },
  /**
   * @TARGET
   * AR pointing
   * @param {String} type = remote.config.AR_POINTING
   * @param {Object} params (문서참조)
   */
  arPointing: (type, params = {}, target = null) => {
    params.type = type
    _.session.signal({
      data: JSON.stringify(params),
      to: target,
      type: SIGNAL.AR_POINTING,
    })
  },
  /**
   * @TARGET
   * request screen capture permission
   * @param {Object} params
   */
  permission: (target = null) => {
    const params = {
      type: 'request',
    }
    _.session.signal({
      type: SIGNAL.CAPTURE_PERMISSION,
      to: target,
      data: JSON.stringify(params),
    })
  },
  /**
   * @TARGET
   * AR drawing
   * @param {String} type = remote.config.AR_DRAWING
   * @param {Object} params (문서참조)
   */
  arDrawing: (type, params = {}, target = null) => {
    if (!_.session) return
    params.type = type
    _.session.signal({
      type: SIGNAL.AR_DRAWING,
      to: target,
      data: JSON.stringify(params),
    })
  },
  /**
   * @BROADCATE
   * @TARGET
   * my video stream control
   * @param {Boolean, String} active true / false / 'NONE'
   */
  video: (active, target = null) => {
    if (!_.publisher) return
    // if (!_.publisher.stream.hasVideo) return
    if (active === 'NONE') {
      active = CAMERA_STATUE.CAMERA_NONE
    } else {
      _.publisher.publishVideo(active)
      active = active ? CAMERA_STATUE.CAMERA_ON : CAMERA_STATUE.CAMERA_OFF
    }

    const params = {
      type: CAMERA.STATUS,
      status: active,
      currentZoomLevel: _.currentZoomLevel,
      maxZoomLevel: _.maxZoomLevel,
    }
    try {
      _.session.signal({
        data: JSON.stringify(params),
        to: target,
        type: SIGNAL.CAMERA,
      })
    } catch (err) {
      return false
    }
  },
  changeProperty: (newValue, target = []) => {
    const params = {
      type: CAMERA.STATUS,
      status: newValue ? CAMERA_STATUE.CAMERA_OFF : CAMERA_STATUE.CAMERA_NONE,
      currentZoomLevel: _.currentZoomLevel,
      maxZoomLevel: _.maxZoomLevel,
    }
    try {
      _.session.signal({
        data: JSON.stringify(params),
        to: target,
        type: SIGNAL.CAMERA,
      })
    } catch (err) {
      return false
    }
  },
  /**
   * @BROADCATE
   * @TARGET
   * my mic control
   * @param {Boolean} active
   */
  mic: (active, target = null) => {
    if (!_.publisher) return
    _.publisher.publishAudio(active)

    const params = {
      isOn: active,
    }
    // console.log('>>>>>>>>>>>>>>>>>>>>>>>>>>>>', target)
    // let custom = []
    // for (let a of target) {
    //   custom.push({
    //     connectionId: a.connectionId,
    //     // creationTime: a.creationTime,
    //     // data: a.data,
    //   })
    // }
    // console.log('>>>>>>>>>>>>>>>>>>>>>>>>>>>>', custom)
    try {
      _.session.signal({
        data: JSON.stringify(params),
        to: target,
        type: SIGNAL.MIC,
      })
    } catch (err) {
      return false
    }
  },
  /**
   * @BROADCATE
   * @TARGET
   * my speaker control
   * @param {Boolean} active
   */
  speaker: (active, target = null) => {
    for (let subscriber of _.subscribers) {
      subscriber.subscribeToAudio(active)
    }
    const params = {
      isOn: active,
    }
    try {
      _.session.signal({
        data: JSON.stringify(params),
        to: target,
        type: SIGNAL.SPEAKER,
      })
    } catch (err) {
      return false
    }
  },
  /**
   * @BROADCATE
   * @TARGET
   * other user's flash control
   * @param {Boolean} active
   */
  flashStatus: (status = FLASH_STATUE.FLASH_NONE, target = null) => {
    const params = {
      status: status,
      type: FLASH.STATUS,
    }
    _.session.signal({
      data: JSON.stringify(params),
      to: target,
      type: SIGNAL.FLASH,
    })
  },
  /**
   * @BROADCATE
   * @TARGET
   * other user's flash control
   * @param {Boolean} active
   * @param {String} id : target id
   */
  flash: (active, target) => {
    const params = {
      enable: active,
      type: FLASH.FLASH,
    }
    _.session.signal({
      data: JSON.stringify(params),
      to: target,
      type: SIGNAL.FLASH,
    })
  },
  /**
   * @BROADCATE
   * @TARGET
   * other user's camera control
   * @param {Boolean} active
   */
  zoom: (level, target) => {
    const params = {
      type: CAMERA.ZOOM,
      level: level,
    }
    _.session.signal({
      data: JSON.stringify(params),
      to: target,
      type: SIGNAL.CAMERA,
    })
  },
  /**
   * @BROADCATE
   * @TARGET
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
   * leave session
   */
  leave: () => {
    if (!_.session) return
    _.session.disconnect()
    _.clear()
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
    _.currentZoomLevel = 1
    _.maxZoomLevel = 1
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
