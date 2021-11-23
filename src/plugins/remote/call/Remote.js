import { OpenVidu } from '@virnect/remote-webrtc'
// import { OpenVidu } from './openvidu'
import { addSessionEventListener } from './RemoteUtils'
import sender from './RemoteSender'
import Store from 'stores/remote/store'
import { SIGNAL, VIDEO, LOCATION } from 'configs/remote.config'
import { URLS, setRecordInfo } from 'configs/env.config'
import { DEVICE, CAMERA as CAMERA_STATUS } from 'configs/device.config'
import { logger, debug } from 'utils/logger'
import { getResolutionScale } from 'utils/settingOptions'
import { wsUri } from 'api/gateway/api'

let OV = null

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

  configs: null,
  options: null,

  stream: null,

  /**
   * join session
   * @param {Object} configs {coturn, wss, token}
   * @param {String} role remote.config.ROLE
   * @param {Object|Boolean} options 장치 device id or false
   * @param {MediaStream} mediaStream 미디어 스트림
   */
  connect: async (configs, role, options, mediaStream) => {
    try {
      _.account = Store.getters['account']

      Store.commit('callClear')

      initOpenVidu()

      const isProduction = process.env.NODE_ENV === 'production'
      const isNotDevelop = !(window.env && window.env === 'develop')

      if (isProduction && isNotDevelop) {
        OV.enableProdMode()
      }

      if (!_.session) {
        _.session = OV.initSession()
        addSessionEventListener(_.session)
      }

      const metaData = {
        clientData: _.account.uuid,
        roleType: role,
        deviceType: DEVICE.WEB,
      }

      // const iceServers = URLS.coturn

      let ws = configs.wss || `${URLS['ws']}${wsUri['REMOTE']}`
      // const ws = 'wss://192.168.6.3:8000/remote/websocket'

      //config 서버에 정의된 coturnUrl 우선 설정
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

      //서버 녹화 정보 설정
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
        role: mediaStream ? 'PUBLISHER' : 'SUBSCRIBER',
      }

      await _.session.connect(
        configs.token,
        connectOption,
        JSON.stringify(metaData),
      )

      Store.dispatch('updateAccount', {
        roleType: role,
      })

      _.account.roleType = role
      _.configs = configs
      _.options = options

      if (mediaStream) {
        await doPublish({ mediaStream, options, configs })
      } else {
        updateParticipantEmpty(_.connectionId)
      }
      return true
    } catch (err) {
      console.error(err)
      throw err
    }
  },

  setScaleResolution: async scaleResolution => {
    if (_.publisher && _.publisher.stream) {
      return await _.publisher.stream.webRtcPeer.setScaleResolution(
        scaleResolution,
      )
    }
  },
  /**
   * @BROADCATE
   * chatting
   * @param {String} text
   */
  sendChat: sender.chat,

  /**
   * @BROADCATE
   * chatting-file
   */
  sendFile: sender.file,

  /**
   * @TARGET
   * resolution
   * @param {Object} resolution = {width, height, orientation}
   */
  sendResolution: sender.resolution,

  /**
   * @BROADCATE
   * main view change (only leader)
   * @param {String} uuid
   * @param {Boolean} force true / false
   */

  // sendVideo: sender.video,

  sendVideo: (uuid, force = false, target = null) => {
    // if (_.account.roleType !== ROLE.LEADER) return
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
  sendPointing: sender.pointing,

  /**
   * @BROADCATE
   * sharing drawing
   * @param {String} type = remote.config.DRAWING
   * @param {Object} params
   */
  sendDrawing: sender.drawing,

  /**
   * @BROADCATE
   * @TARGET
   * other user's pointing, recording control
   * @param {String} type = remote.config.CONTROL
   */
  sendControl: sender.control,

  /**
   * @BROADCATE
   * @TARGET
   * other user's pointing, recording control
   * @param {String} type = remote.config.CONTROL
   */
  sendControlRestrict: sender.controlRestrict,

  /**
   * @BROADCATE
   * AR feature status
   * @param {String} type = remote.config.AR_FEATURE
   */
  sendArFeatureStart: sender.arFeatureStart,

  /**
   * @BROADCATE
   * AR feature status
   * @param {String} type = remote.config.AR_FEATURE
   */
  sendArFeatureStop: sender.arFeatureStop,

  /**
   * @TARGET
   * AR pointing
   * @param {String} type = remote.config.AR_POINTING
   * @param {Object} params (문서참조)
   */
  sendArPointing: sender.arPointing,

  /**
   * @TARGET
   * request screen capture permission
   * @param {Object} params
   */
  sendCapturePermission: sender.capturePermission,

  /**
   * @TARGET
   * AR drawing
   * @param {String} type = remote.config.AR_DRAWING
   * @param {Object} params (문서참조)
   */
  sendArDrawing: sender.arDrawing,

  /**
   * @TARGET
   * AR drawing
   * @param {String} type = remote.config.AR_3D
   * @param {Object} params (문서참조)
   */
  sendAr3dSharing: sender.ar3dContentShare,

  /**
   * @BROADCATE
   * @TARGET
   * my video stream control
   * @param {Boolean, String} status CAMERA_STATUS
   * @param {Boolean} publish video publish 여부. 기본값 true
   */
  sendCamera: sender.camera,

  /**
   * @BROADCATE
   * @TARGET
   * my mic control
   * @param {Boolean} active
   */
  sendMic: sender.mic,

  /**
   * @BROADCATE
   * @TARGET
   * my speaker control
   * @param {Boolean} active
   */
  sendSpeaker: sender.speaker,

  /**
   * @BROADCATE
   * @TARGET
   * other user's flash control
   * @param {Boolean} active
   */
  sendFlashStatus: sender.flashStatus,

  /**
   * @BROADCATE
   * @TARGET
   * other user's flash control
   * @param {Boolean} active
   * @param {String} id : target id
   */
  sendFlash: sender.flash,

  /**
   * @BROADCATE
   * @TARGET
   * other user's camera control
   * @param {Boolean} active
   */
  sendCameraZoom: sender.cameraZoom,

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
   * @param {Boolean} enable 화면 공유 기능 사용 여부 true, false
   * @param {Array[String]} target 신호를 보낼 대상 커넥션 id String 배열
   */

  // sendScreenSharing: sender.screenSharing,

  sendScreenSharing: (enable, target = null) => {
    const params = {
      type: VIDEO.SCREEN_SHARE,
      enable: enable,
    }
    _.session.signal({
      type: SIGNAL.VIDEO,
      to: target,
      data: JSON.stringify(params),
    })
  },

  /**
   * @TARGET
   * 지정한 참가자에게 위치 요청
   */
  sendRequestLocation: (isRefresh = false, target) => {
    const params = {
      type: LOCATION.REQUEST,
      isRefresh: isRefresh,
    }
    _.session.signal({
      data: JSON.stringify(params),
      to: target,
      type: SIGNAL.LOCATION,
    })
  },

  /**
   * @TARGET
   * 지정한 참가자에게 위치 그만 보내라는 요청
   */
  sendStopLocation: target => {
    const params = {
      type: LOCATION.STOP_SEND,
    }
    _.session.signal({
      data: JSON.stringify(params),
      to: target,
      type: SIGNAL.LOCATION,
    })
  },

  setFrameRate: async fps => {
    if (_.publisher && _.publisher.stream) {
      const track = _.publisher.stream.getVideoTracks()[0]
      await track.applyConstraints({
        frameRate: {
          max: fps,
        },
      })
    }
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
   */
  async replaceTrack(track) {
    await _.publisher.replaceTrack(track)
    await new Promise(resolve => setTimeout(resolve, 200))
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

    const settingInfo = Store.getters['settingInfo']

    await track.applyConstraints({
      frameRate: settingInfo.fps ? settingInfo.fps : 30,
    })

    _.sendResolution({
      width: settings.width,
      height: settings.height,
      orientation: '',
    })
  },

  /**
   * 주어진 비디오 트랙으로 기존 publisher를 초기화하고 다시
   * initPublisher를 실행.
   *
   * @param {MediaStreamTrack} videoTrack 교체할 비디오 트랙
   * @returns {Boolean} 성공 여부
   */
  async rePublish({ videoSource = false, audioSource }) {
    try {
      const settingInfo = Store.getters['settingInfo']

      if (videoSource || audioSource) {
        const publishOptions = getPublishOptions({
          audioSource: audioSource,
          videoSource: videoSource,
          configs: _.configs,
          settingInfo: settingInfo,
          isRepublish: true,
        })

        debug('call::republish', publishOptions)

        const rePublisher = OV.initPublisher('', publishOptions)
        const iceStateChangedCallBack = getIceStateChangedCallBack(rePublisher)
        rePublisher.onIceStateChanged(iceStateChangedCallBack)

        const streamCreatedCallBack = getStreamCreatedCallBack({
          publisher: rePublisher,
          configs: _.configs,
          isRepublish: true,
        })
        rePublisher.on('streamCreated', streamCreatedCallBack)

        if (_.publisher) {
          await _.session.unpublish(_.publisher)
          _.publisher = null
        }

        _.publisher = rePublisher
        _.session.publish(_.publisher)
      } else {
        //republish 과정에서 비디오, 마이크가 없는경우
        if (_.publisher) {
          await _.session.unpublish(_.publisher)
          _.publisher = null
        }

        const mainView = Store.getters['mainView']

        if (mainView.connectionId === _.connectionId) {
          Store.commit('clearMainView', _.connectionId)
        }

        updateParticipantEmpty(_.connectionId)
      }

      return true
    } catch (err) {
      console.error(err)
      throw err
    }
  },

  /**
   * 오디오 디바이스, 비디오 디바이스에 접근하여 오디오 트랙, 비디오 트랙을
   * 하나의 미디어 스트림으로 합쳐서 반환
   *
   * 협업시 오디오 디바이스는 필수임으로 오디오 디바이스가 없으면 에러 반환
   *
   * @param {Object} options 옵션 객체
   * @returns {MediaStream | null} 미디어 스트림 반환
   * @public
   * @throws nodevice
   */
  getStream: async options => {
    let mediaStream = null
    const settingInfo = Store.getters['settingInfo']

    const { audioSource, videoSource } = options

    //오디오, 비디오 둘다 사용하지 않을때,
    if (audioSource === false && videoSource === false) {
      return null
    }

    //get audio stream
    const audioStream = await getMediaStream({
      audioSource: audioSource,
      videoSource: false,
    })

    if (!audioStream) {
      throw 'nodevice'
    }

    //get video stream
    const videoStream = await getMediaStream({
      audioSource: false,
      videoSource: videoSource,
      resolution: settingInfo.quality,
      frameRate: settingInfo.fps ? settingInfo.fps : 30,
    })

    mediaStream = new MediaStream()
    if (videoStream) {
      mediaStream.addTrack(audioStream.getAudioTracks()[0])
      mediaStream.addTrack(videoStream.getVideoTracks()[0])
    } else {
      mediaStream.addTrack(audioStream.getAudioTracks()[0])
    }

    return mediaStream
  },
}

/**
 * publish 수행
 * OV.initPublisher를 실행하고 콜백 함수를 설정
 * @param {Object} Object
 * @param {Object} Object.options
 * @param {Object} Object.configs
 */
const doPublish = async ({ mediaStream, configs }) => {
  const settingInfo = Store.getters['settingInfo']

  const audioTracks = mediaStream.getAudioTracks()
  const videoTracks = mediaStream.getVideoTracks()

  const audioTrack = audioTracks.length > 0 ? audioTracks[0] : false
  const videoTrack = videoTracks.length > 0 ? videoTracks[0] : false

  //퍼블리시 옵션 셋팅
  const publishOptions = getPublishOptions({
    audioSource: audioTrack,
    videoSource: videoTrack,
    configs: configs,
    settingInfo: settingInfo,
    isRepublish: false,
  })

  debug('call::publish::', publishOptions)

  _.publisher = OV.initPublisher('', publishOptions)

  //ice 상태 변경 관련 콜백 설정
  const iceStateChangedCallBack = getIceStateChangedCallBack(_.publisher)
  _.publisher.onIceStateChanged(iceStateChangedCallBack)

  //streamCreated 관련 콜백 파라미터
  const streamCreatedParams = {
    publisher: _.publisher,
    configs: configs,
    isRepublish: false,
  }

  const streamCreatedCallBack = getStreamCreatedCallBack(streamCreatedParams)
  _.publisher.on('streamCreated', streamCreatedCallBack)

  _.session.publish(_.publisher)
}

/**
 * PublisherProperties 설정을 위한 객체 반환
 * @param {Object} Object 설정을 위한 객체
 * @param {MediaStreamTrack|string} Object.audioSource 오디오 소스 or device id
 * @param {MediaStreamTrack|string} Object.videoSource 비디오 소스 or device id
 * @param {Object} Object.configs 설정 정보
 * @param {Object} Object.settingInfo 설정 정보
 * @param {Boolean} Object.isRepublish republish 여부
 * @returns {Object} PublisherProperties 객체
 */
const getPublishOptions = ({
  audioSource,
  videoSource,
  configs,
  settingInfo,
  isRepublish = false,
}) => {
  const publishOptions = {
    // audioSource: options.audioSource,
    // videoSource: options.videoSource,
    audioSource: audioSource,
    videoSource: videoSource,
    publishAudio: configs.audioRestrictedMode ? false : settingInfo.micOn,
    publishVideo: configs.videoRestrictedMode ? false : settingInfo.videoOn,
    resolution: settingInfo.quality,
    // resolution: '1920x1080', // FHD
    // resolution: '3840x2160', // 4K
    frameRate: 30,
    insertMode: 'PREPEND',
    mirror: false,
  }

  if (isRepublish) {
    publishOptions.publishVideo = videoSource ? true : false
  }

  return publishOptions
}

/**
 * iceStateChanged 이벤트 리스너 콜백 함수 반환
 * @param {Object} publisher publisher 객체
 * @returns iceStateChanged 콜백 함수
 */
const getIceStateChangedCallBack = publisher => {
  return state => {
    if (['failed', 'disconnected', 'closed'].includes(state)) {
      Store.commit('updateParticipant', {
        connectionId: publisher.stream.connection.connectionId,
        status: 'disconnected',
      })
    } else if (['connected', 'completed'].includes(state)) {
      Store.commit('updateParticipant', {
        connectionId: publisher.stream.connection.connectionId,
        status: 'good',
      })
    } else {
      Store.commit('updateParticipant', {
        connectionId: publisher.stream.connection.connectionId,
        status: 'normal',
      })
    }
    logger('ice state change', state)
  }
}

/**
 * streamCreated 이벤트에 실행될 콜백 함수 생성
 * @param {Object} Object 설정을 위한 객체
 * @param {Object} Object.publisher publisher 객체
 * @param {Boolean} Object.isRepublish republish 유무
 * @param {Object} Object.configs 컨피그 객체
 * @returns streamCreated 이벤트에 실행될 콜백 함수
 */
const getStreamCreatedCallBack = ({ publisher, isRepublish, configs }) => {
  return () => {
    const logText = isRepublish ? 're' : ''
    logger('room', logText + 'publish success')
    debug(logText + 'publisher stream :: ', publisher.stream)
    const mediaStream = publisher.stream.mediaStream

    const video = Store.getters['video']
    const quality = Number.parseInt(video.quality, 10)
    const scale = getResolutionScale(quality)
    _.setScaleResolution(scale)

    //자신의 스트림에 video가 있다면
    // -> 비디오 제한 모드인지 체크
    //    -> 비디오 제한 모드이면 CAMERA_OFF
    // -> 제한 모드가 아니면 video 활성화(active) 체크
    //     -> 활성화일때 CAMERA_ON
    //     -> 비활성화일때 CAMERA_OFF
    //자신의 스트림에 video가 없다면 CAMERA_NONE
    const cameraStatus = publisher.stream.hasVideo
      ? configs.videoRestrictedMode
        ? CAMERA_STATUS.CAMERA_OFF
        : publisher.stream.videoActive
        ? CAMERA_STATUS.CAMERA_ON
        : CAMERA_STATUS.CAMERA_OFF
      : CAMERA_STATUS.CAMERA_NONE

    const participantInfo = {
      connectionId: publisher.stream.connection.connectionId,
      stream: mediaStream,
      hasVideo: publisher.stream.hasVideo,
      hasCamera: publisher.stream.hasVideo,
      hasAudio: publisher.stream.hasAudio,
      video: publisher.stream.videoActive, // settingInfo.videoOn,
      audio: publisher.stream.audioActive,
      cameraStatus: cameraStatus,
    }

    if (isRepublish) {
      delete participantInfo.hasCamera
    }

    Store.commit('updateParticipant', participantInfo)

    if (isRepublish) {
      _.sendCamera(
        publisher.stream.hasVideo
          ? CAMERA_STATUS.CAMERA_ON
          : CAMERA_STATUS.CAMERA_NONE,
      )
    }

    if (publisher.stream.hasVideo) {
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
        _.maxZoomLevel = parseInt(capability.zoom.max / capability.zoom.min, 10)
        _.minZoomLevel = parseInt(capability.zoom.min, 10)
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
    }
  }
}

/**
 * 특정 participant정보를 false 및 CAMERA_STATUS.NONE 으로 업데이트
 * @param {String} connectionId 커넥션 id
 */
const updateParticipantEmpty = connectionId => {
  Store.commit('updateParticipant', {
    connectionId: connectionId,
    cameraStatus: CAMERA_STATUS.CAMERA_NONE,
    hasVideo: false,
    hasAudio: false,
    video: false,
    audio: false,
  })
}

/**
 * 오디오 or 비디오 장치에 접근하여 스트림 반환
 * @param {Object} options PublisherProperties
 * @returns {MediaStream|null} 오디오 or 비디오 스트림 or null
 *
 */
const getMediaStream = async options => {
  let mediaStream = null
  try {
    initOpenVidu()
    mediaStream = await OV.getUserMedia(options)
  } catch (e) {
    if (e && e.name === 'DEVICE_ACCESS_DENIED') {
      logger('getMediaStream', 'DEVICE_ACCESS_DENIED')
    } else {
      console.error(e)
    }
  }

  return mediaStream
}

const initOpenVidu = () => {
  if (OV === null) {
    OV = new OpenVidu()
  }
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
