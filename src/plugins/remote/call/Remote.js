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
import { getPermission, getUserMedia } from 'utils/deviceCheck'

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
  /**
   * join session
   * @param {Object} configs {coturn, wss, token}
   * @param {String} role remote.config.ROLE
   */
  connect: async (configs, role) => {
    // const publishVideo = role !== ROLE.LEADER
    const devices = await navigator.mediaDevices.enumerateDevices()
    const hasVideo =
      devices.findIndex(device => device.kind.toLowerCase() === 'videoinput') >
      -1
    const hasAudio =
      devices.findIndex(device => device.kind.toLowerCase() === 'audioinput') >
      -1

    if (!hasAudio && !hasVideo) {
      throw 'nodevice'
    }
    _.account = Store.getters['account']
    const settingInfo = Store.getters['settingInfo']
    let audioSource =
      devices.findIndex(device => device.deviceId === settingInfo.mic) > -1
        ? settingInfo.mic
        : undefined
    let videoSource = hasVideo
      ? devices.findIndex(device => device.deviceId === settingInfo.video) > -1
        ? settingInfo.video
        : undefined
      : false

    const permission = await getPermission()
    if (permission === 'promt') {
      await getUserMedia(true, hasVideo)
    } else if (permission !== true) {
      throw permission
    }

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
      _.account.roleType = role

      const publishOptions = {
        audioSource: audioSource,
        videoSource: videoSource,
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
        if (_.publisher.properties.publishVideo) {
          const track = mediaStream.getVideoTracks()[0]
          const settings = track.getSettings()
          const capability = track.getCapabilities()
          logger('call', `resolution::${settings.width}X${settings.height}`)
          debug('call::setting::', settings)
          debug('call::capability::', capability)
          if ('zoom' in capability) {
            track.applyConstraints({
              advanced: [{ zoom: 100 }],
            })
            _.maxZoomLevel = parseInt(capability.zoom.max / 100)
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
      console.error(err)
      return false
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
   * main view change (only leader)
   * @param {String} uuid
   * @param {Boolean} force true / false
   */
  mainview: (uuid, force = false) => {
    if (_.account.roleType !== ROLE.LEADER) return
    if (!uuid) uuid = _.account.uuid
    const params = {
      id: uuid,
      type: force ? VIDEO.SHARE : VIDEO.NORMAL,
    }
    _.session.signal({
      data: JSON.stringify(params),
      to: _.session.connection,
      type: SIGNAL.VIDEO,
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
  startArFeature: targetId => {
    const params = {
      type: AR_FEATURE.START_AR_FEATURE,
      targetUserId: targetId,
    }
    _.session.signal({
      data: JSON.stringify(params),
      to: _.session.connection,
      type: SIGNAL.AR_FEATURE,
    })
  },
  /**
   * AR feature status
   * @param {String} type = remote.config.AR_FEATURE
   */
  stopArFeature: () => {
    const params = {
      type: AR_FEATURE.STOP_AR_FEATURE,
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
  video: active => {
    if (!_.publisher) return
    if (!_.publisher.stream.hasVideo) return
    _.publisher.publishVideo(active)

    const params = {
      type: CAMERA.STATUS,
      status: active ? CAMERA_STATUE.CAMERA_ON : CAMERA_STATUE.CAMERA_OFF,
      currentZoomLevel: _.currentZoomLevel,
      maxZoomLevel: _.maxZoomLevel,
    }
    try {
      _.session.signal({
        data: JSON.stringify(params),
        to: _.session.connection,
        type: SIGNAL.CAMERA,
      })
    } catch (err) {
      return false
    }
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
  flashStatus: (status = FLASH_STATUE.FLASH_NONE) => {
    const params = {
      status: status,
      from: _.account.uuid,
      type: FLASH.STATUS,
    }
    _.session.signal({
      data: JSON.stringify(params),
      to: _.session.connection,
      type: SIGNAL.FLASH,
    })
  },
  /**
   * other user's flash control
   * @param {Boolean} active
   * @param {String} id : target id
   */
  flash: (active, id) => {
    const params = {
      enable: active,
      from: _.account.uuid,
      type: FLASH.FLASH,
      to: [id],
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
  zoom: (level, id) => {
    const params = {
      from: _.account.uuid,
      type: CAMERA.ZOOM,
      level: level,
      to: [id],
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
   * leave session
   */
  leave: () => {
    try {
      if (!_.session) return
      _.session.disconnect()
      _.clear()
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
