import { OpenVidu } from '@virnect/remote-webrtc'
import { addSessionEventListener } from './RemoteUtils'
import sender from './RemoteSender'
import Store from 'stores/remote/store'
import { SIGNAL, CAMERA, VIDEO } from 'configs/remote.config'
import { URLS, setRecordInfo } from 'configs/env.config'
import { DEVICE, CAMERA as CAMERA_STATUS } from 'configs/device.config'
import { logger, debug } from 'utils/logger'
import { wsUri } from 'api/gateway/api'

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

  configs: null,
  options: null,

  /**
   * join session
   * @param {Object} configs {coturn, wss, token}
   * @param {String} role remote.config.ROLE
   */
  connect: async (configs, role, options) => {
    try {
      _.account = Store.getters['account']

      Store.commit('callClear')

      OV = new OpenVidu()

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
        role: options === false ? 'SUBSCRIBER' : 'PUBLISHER',
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

      if (options !== false) {
        await doPublish({ options, configs })
      } else {
        updateParticipantEmpty(_.connectionId)
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
  sendVideo: sender.video,

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
   * 현재 전체 공유중인 360 스트림의 제어 정보를 전송
   * @BROADCATE
   * @TARGET
   * @param {Object} info 제어정보(yaw, pitch)
   */
  sendPanoRotation: sender.panoRotation,

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
}

/**
 * publish 수행
 * OV.initPublisher를 실행하고 콜백 함수를 설정
 * @param {Object} Object
 * @param {Object} Object.options
 * @param {Object} Object.configs
 */
const doPublish = async ({ options, configs }) => {
  const settingInfo = Store.getters['settingInfo']

  const mediaStream = await OV.getUserMedia({
    audioSource: options ? options.audioSource : false,
    videoSource: options ? options.videoSource : false,
    resolution: settingInfo.quality,
    frameRate: 30,
  })

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

  //ice 상태 변경 관련 콜백
  const iceStateChangedCallBack = getIceStateChangedCallBack(_.publisher)
  _.publisher.onIceStateChanged(iceStateChangedCallBack)

  //streamCreated 관련 콜백
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
