import { OpenVidu } from '@virnect/remote-webrtc'
import { addSessionEventListener } from './RemoteUtils'
import Store from 'stores/remote/store'
import {
  SIGNAL,
  ROLE,
  CAMERA,
  FLASH,
  VIDEO,
  AR_FEATURE,
  FILE,
  CONTROL,
} from 'configs/remote.config'
import { URLS, setRecordInfo } from 'configs/env.config'
import {
  DEVICE,
  FLASH as FLASH_STATUS,
  CAMERA as CAMERA_STATUS,
} from 'configs/device.config'
import { logger, debug } from 'utils/logger'
import { wsUri } from 'api/gateway/api'
import { checkInput } from 'utils/deviceCheck'

let OV

const _ = {
  account: null,
  session: null,
  publisher: null,
  connectionId: '',
  subscribers: [],
  // 필요여부 체크할 것
  resolution: null,
  currentZoomLevel: 1,
  maxZoomLevel: 1,
  openRoom: false,

  configs: null,
  options: null,

  /**
   * join session
   * @param {Object} configs {coturn, wss, token}
   * @param {String} role remote.config.ROLE
   */
  connect: async (configs, role, options, open = false) => {
    try {
      _.account = Store.getters['account']
      _.openRoom = open
      _.configs = configs
      _.options = options

      Store.commit('callClear')
      OV = new OpenVidu()
      if (process.env.NODE_ENV === 'production') {
        OV.enableProdMode()
      }
      if (!_.session) {
        _.session = OV.initSession()
        addSessionEventListener(_.session, Store)
      }

      const metaData = {
        clientData: _.account.uuid,
        roleType: role,
        deviceType: DEVICE.WEB,
      }

      // const iceServers = URLS.coturn
      let ws = configs.wss || `${URLS['ws']}${wsUri['REMOTE']}`
      // const ws = 'wss://192.168.6.3:8000/remote/websocket'
      if (URLS['coturnUrl']) {
        for (let config of configs.coturn) {
          config.url = URLS['coturnUrl']
        }
        ws = `${URLS['ws']}${wsUri['REMOTE']}` || configs.wss
      }

      const iceServers = configs.coturn
      for (let ice of iceServers) {
        ice['urls'] = ice['url']
      }

      setRecordInfo({
        token: configs['token'],
        coturn: iceServers,
        wss: ws,
      })

      if (!iceServers) {
        throw 'ice server를 찾을 수 없습니다.'
      }
      debug('coturn::', iceServers)

      if (configs.audioRestrictedMode || configs.videoRestrictedMode) {
        Store.commit('setRestrictedMode', true)
        Store.dispatch('setDevices', {
          video: {
            isOn: false,
          },
          audio: {
            isOn: false,
          },
        })
      }

      const connectOption = {
        iceServers,
        wsUri: ws,
        role: options === false ? 'SUBSCRIBER' : 'PUSLISHER',
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

      if (options !== false) {
        const settingInfo = Store.getters['settingInfo']

        const publishOptions = {
          // audioSource: options.audioSource,
          // videoSource: options.videoSource,
          audioSource: options ? options.audioSource : false,
          videoSource: options ? options.videoSource : false,
          publishAudio: configs.audioRestrictedMode ? false : settingInfo.micOn,
          publishVideo: configs.videoRestrictedMode
            ? false
            : settingInfo.videoOn,
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
          if (['failed', 'disconnected', 'closed'].includes(state)) {
            Store.commit('updateParticipant', {
              connectionId: _.publisher.stream.connection.connectionId,
              status: 'disconnected',
            })
          } else if (['connected', 'completed'].includes(state)) {
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
          debug('publisher stream :: ', _.publisher.stream)
          const mediaStream = _.publisher.stream.mediaStream

          const participantInfo = {
            connectionId: _.publisher.stream.connection.connectionId,
            stream: mediaStream,
            hasVideo: _.publisher.stream.hasVideo,
            hasCamera: _.publisher.stream.hasVideo,
            hasAudio: _.publisher.stream.hasAudio,
            video: _.publisher.stream.videoActive, // settingInfo.videoOn,
            audio: _.publisher.stream.audioActive,
            cameraStatus: _.publisher.stream.hasVideo
              ? configs.videoRestrictedMode
                ? CAMERA_STATUS.CAMERA_OFF
                : _.publisher.stream.videoActive
                ? CAMERA_STATUS.CAMERA_ON
                : CAMERA_STATUS.CAMERA_OFF
              : CAMERA_STATUS.CAMERA_NONE,
          }

          Store.commit('updateParticipant', participantInfo)
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
              _.maxZoomLevel = parseInt(
                capability.zoom.max / capability.zoom.min,
              )
              _.minZoomLevel = parseInt(capability.zoom.min)
            }
            // _.sendCamera(
            //   options.videoSource !== false
            //     ? settingInfo.videoOn
            //       ? CAMERA_STATUS.CAMERA_ON
            //       : CAMERA_STATUS.CAMERA_OFF
            //     : CAMERA_STATUS.CAMERA_NONE,
            // )
            _.sendResolution({
              width: settings.width,
              height: settings.height,
              orientation: '',
            })
          } else if (_.openRoom) {
            checkInput({ video: true, audio: false }).then(hasCamera => {
              const params = {
                connectionId: _.publisher.stream.connection.connectionId,
                hasAudio: true,
              }
              if (!hasCamera) {
                params.cameraStatus = CAMERA_STATUS.CAMERA_NONE
                params.hasCamera = false
                // _.changeProperty(true)
              } else {
                params.cameraStatus = CAMERA_STATUS.CAMERA_OFF
                params.hasCamera = true
              }
              Store.commit('updateParticipant', params)
              // _.sendCamera(
              //   !hasCamera ? CAMERA_STATUS.CAMERA_NONE : CAMERA_STATUS.CAMERA_OFF,
              // )
            })
          }
        })

        _.session.publish(_.publisher)
      } else {
        Store.commit('updateParticipant', {
          connectionId: _.connectionId,
          cameraStatus: CAMERA_STATUS.CAMERA_NONE,
          hasVideo: false,
          hasAudio: false,
          video: false,
          audio: false,
        })
      }
      return true
    } catch (err) {
      console.error(err)
      throw err
    }
  },
  /**
   * @BROADCATE
   * chatting
   * @param {String} text
   */
  sendChat: (text, code = 'ko-KR') => {
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
    params.type = FILE.UPLOADED

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
  sendVideo: (uuid, force = false, target = null) => {
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
  sendPointing: params => {
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
  sendDrawing: (type, params = {}, target = null) => {
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
  sendControl: (type, enable, target = null) => {
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
   * @TARGET
   * other user's pointing, recording control
   * @param {String} type = remote.config.CONTROL
   */
  sendControlRestrict: (device, enable, target = null) => {
    const params = {
      type: CONTROL.RESTRICTED_MODE,
      target: device,
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
  sendArFeatureStart: targetId => {
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
  sendArFeatureStop: () => {
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
  sendArPointing: (type, params = {}, target = null) => {
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
  sendCapturePermission: (target = null) => {
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
  sendArDrawing: (type, params = {}, target = null) => {
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
   * @param {Boolean, String} status CAMERA_STATUS
   * @param {Boolean} publish video publish 여부. 기본값 true
   */
  sendCamera: (
    status = CAMERA_STATUS.CAMERA_NONE,
    target = null,
    publish = true,
  ) => {
    console.log('sendCamera')
    console.log('sendCameraStatus', status)
    console.trace()
    if (!_.publisher) return
    // if (!_.publisher.stream.hasVideo) return
    if (
      status === CAMERA_STATUS.CAMERA_ON ||
      status === CAMERA_STATUS.CAMERA_OFF
    ) {
      if (publish) {
        _.publisher.publishVideo(status === CAMERA_STATUS.CAMERA_ON)
      }
    }

    const params = {
      type: CAMERA.STATUS,
      status: status,
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
  sendMic: (active, target = null) => {
    // if (_.openRoom) return
    if (_.publisher) {
      _.publisher.publishAudio(active)
    }

    const params = {
      isOn: active,
    }
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
  sendSpeaker: (active, target = null) => {
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
  sendFlashStatus: (status = FLASH_STATUS.FLASH_NONE, target = null) => {
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
  sendFlash: (active, target) => {
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
  sendCameraZoom: (level, target) => {
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
  /**
   * 화면 공유 여부
   * 현재는 false 만 보냄. 필요시 true 보낼 수 있음.
   * @param {Boolean} enable 화면 공유 기능 중단 여부 true, false
   */
  sendScreenSharing: enable => {
    const params = {
      type: VIDEO.SCREEN_SHARE,
      enable: enable,
    }
    _.session.signal({
      type: SIGNAL.VIDEO,
      to: null,
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
    if (_.session) {
      _.session.off(type, func)
    }
  },
  /**
   * 현재 자신의 비디오 트랙을 교체할 비디오 트랙으로 변경
   *
   * @param {MediaStreamTrack} track 교체할 비디오 트랙
   * @param {MediaStream} originStream 보존할 원래 스트림
   */
  replaceTrack(track, originStream) {
    if (originStream) {
      Store.commit('setMyTempStream', originStream.clone())
    }
    _.publisher.replaceTrack(track)
  },

  /**
   * 내 스트림을 보존되어 있는 스트림으로 변경
   * @param {Boolean} play 보존 되어있는 스트림의 재생 처리 여부
   */
  restoreMyStream(play) {
    const myTempStream = Store.getters['myTempStream']

    if (myTempStream) {
      const videoTrack = myTempStream.getVideoTracks()[0]
      if (play) {
        videoTrack.enabled = true
      }
      _.replaceTrack(videoTrack)
    } else {
      _.replaceTrack(_.getDummyVideTrack())
    }

    Store.commit('setMyTempStream', null)
  },

  getDummyVideTrack() {
    let canvas = Object.assign(document.createElement('canvas'), {
      width: 640,
      height: 480,
    })
    canvas.getContext('2d').fillStyle = 'BLUE'
    canvas.getContext('2d').fillRect(0, 0, 640, 480)
    let stream = canvas.captureStream()
    return Object.assign(stream.getVideoTracks()[0], { enabled: false })
  },
  /**
   * 주어진 비디오 트랙으로 기존 publisher를 초기화하고 다시
   * initPublisher를 실행.
   *
   * @param {MediaStreamTrack} videoTrack
   */
  async rePublish(videoTrack) {
    try {
      let videoSource = false

      if (videoTrack) {
        videoSource = videoTrack
      } else {
        videoSource = _.options ? _.options.videoSource : false
      }

      const audioSource = _.options ? _.options.audioSource : false
      const settingInfo = Store.getters['settingInfo']

      if (videoSource || audioSource) {
        const publishOptions = {
          audioSource: audioSource,
          videoSource: videoSource,
          publishAudio: _.configs.audioRestrictedMode
            ? false
            : settingInfo.micOn,
          publishVideo: videoTrack ? true : false,
          resolution: settingInfo.quality,
          // resolution: '1920x1080', // FHD
          // resolution: '3840x2160', // 4K
          frameRate: 30,
          insertMode: 'PREPEND',
          mirror: false,
        }
        debug('call::republish::', publishOptions)

        const tempPublisher = OV.initPublisher('', publishOptions)
        tempPublisher.onIceStateChanged(state => {
          if (['failed', 'disconnected', 'closed'].includes(state)) {
            Store.commit('updateParticipant', {
              connectionId: tempPublisher.stream.connection.connectionId,
              status: 'disconnected',
            })
          } else if (['connected', 'completed'].includes(state)) {
            Store.commit('updateParticipant', {
              connectionId: tempPublisher.stream.connection.connectionId,
              status: 'good',
            })
          } else {
            Store.commit('updateParticipant', {
              connectionId: tempPublisher.stream.connection.connectionId,
              status: 'normal',
            })
          }
          logger('ice state change', state)
        })
        tempPublisher.on('streamCreated', () => {
          logger('room', 'publish success')
          debug('publisher stream :: ', tempPublisher.stream)
          const mediaStream = tempPublisher.stream.mediaStream

          const participantInfo = {
            connectionId: tempPublisher.stream.connection.connectionId,
            stream: mediaStream,
            hasVideo: tempPublisher.stream.hasVideo,
            hasCamera: tempPublisher.stream.hasVideo,
            hasAudio: tempPublisher.stream.hasAudio,
            video: tempPublisher.stream.videoActive, // settingInfo.videoOn,
            audio: tempPublisher.stream.audioActive,
            cameraStatus: tempPublisher.stream.hasVideo
              ? _.configs.videoRestrictedMode
                ? CAMERA_STATUS.CAMERA_OFF
                : tempPublisher.stream.videoActive
                ? CAMERA_STATUS.CAMERA_ON
                : CAMERA_STATUS.CAMERA_OFF
              : CAMERA_STATUS.CAMERA_NONE,
          }

          Store.commit('updateParticipant', participantInfo)
          if (tempPublisher.stream.hasVideo) {
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
              _.maxZoomLevel = parseInt(
                capability.zoom.max / capability.zoom.min,
              )
              _.minZoomLevel = parseInt(capability.zoom.min)
            }

            _.sendResolution({
              width: settings.width,
              height: settings.height,
              orientation: '',
            })
          } else if (_.openRoom) {
            checkInput({ video: true, audio: false }).then(hasCamera => {
              const params = {
                connectionId: tempPublisher.stream.connection.connectionId,
                hasAudio: true,
              }
              if (!hasCamera) {
                params.cameraStatus = CAMERA_STATUS.CAMERA_NONE
                params.hasCamera = false
                // _.changeProperty(true)
              } else {
                params.cameraStatus = CAMERA_STATUS.CAMERA_OFF
                params.hasCamera = true
              }
              Store.commit('updateParticipant', params)
              // _.sendCamera(
              //   !hasCamera ? CAMERA_STATUS.CAMERA_NONE : CAMERA_STATUS.CAMERA_OFF,
              // )
            })
          }
        })

        if (_.publisher) {
          await _.session.unpublish(_.publisher)
          _.publisher = null
        }

        _.publisher = tempPublisher
        _.session.publish(_.publisher)
      } else {
        //이 경우는 리퍼블리시 하려고 하는데 비디오, 마이크가 없는경우임
        //즉 고려하지 않아도 되는 경우이며 삭제해야함
        if (_.publisher) {
          await _.session.unpublish(_.publisher)
          _.publisher = null
        }

        const mainView = Store.getters['mainView']

        if (mainView.connectionId === _.connectionId) {
          Store.commit('clearMainView', _.connectionId)
        }

        Store.commit('updateParticipant', {
          connectionId: _.connectionId,
          cameraStatus: CAMERA_STATUS.CAMERA_NONE,
          hasVideo: false,
          hasAudio: false,
          video: false,
          audio: false,
        })
      }

      return true
    } catch (err) {
      console.error(err)
      throw err
    }
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
