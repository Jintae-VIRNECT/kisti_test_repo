/**
 * session signal을 보내는 함수 정의
 * @see https://www.notion.so/virnect/Remote-2-0-Session-Signal-b2f5c765a16843f8b33bbb179e677def
 */

import _ from './Remote'
import {
  SIGNAL,
  ROLE,
  CAMERA,
  FLASH,
  VIDEO,
  AR_FEATURE,
  FILE,
  CONTROL,
  APP,
} from 'configs/remote.config'
import {
  FLASH as FLASH_STATUS,
  CAMERA as CAMERA_STATUS,
} from 'configs/device.config'

import Store from 'stores/remote/store'

/**
 * @BROADCATE
 * chatting
 * @param {String} text
 */
const chat = (text, code = 'ko-KR') => {
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
}

/**
 * @BROADCATE
 * chatting-file
 */
const file = params => {
  if (!_.session) return
  params.type = FILE.UPLOADED

  //파일 관련 정보 전송하기
  _.session.signal({
    data: JSON.stringify(params),
    to: null,
    type: SIGNAL.FILE,
  })
}

/**
 * @TARGET
 * resolution
 * @param {Object} resolution = {width, height, orientation}
 */
const resolution = (resolution, target = null) => {
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
}

/**
 * @BROADCATE
 * main view change (only leader)
 * @param {String} uuid
 * @param {Boolean} force true / false
 */
const video = (uuid, force = false, target = null) => {
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
}

/**
 * @BROADCATE
 * pointing
 * @param {Object} params
 *  = {color, opacity, width, posX, posY}
 */
const pointing = params => {
  if (!_.session) return
  _.session.signal({
    data: JSON.stringify(params),
    to: null,
    type: SIGNAL.POINTING,
  })
}

/**
 * @BROADCATE
 * sharing drawing
 * @param {String} type = remote.config.DRAWING
 * @param {Object} params
 */
const drawing = (type, params = {}, target = null) => {
  params.type = type
  _.session.signal({
    type: SIGNAL.DRAWING,
    to: target,
    data: JSON.stringify(params),
  })
}

/**
 * @BROADCATE
 * @TARGET
 * other user's pointing, recording control
 * @param {String} type = remote.config.CONTROL
 */
const control = (type, enable, target = null) => {
  const params = {
    type,
    enable,
  }
  _.session.signal({
    data: JSON.stringify(params),
    to: target,
    type: SIGNAL.CONTROL,
  })
}

/**
 * @BROADCATE
 * @TARGET
 * other user's pointing, recording control
 * @param {String} type = remote.config.CONTROL
 */
const controlRestrict = (device, enable, target = null) => {
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
}

/**
 * @BROADCATE
 * AR feature status
 * @param {String} type = remote.config.AR_FEATURE
 */
const arFeatureStart = targetId => {
  const params = {
    type: AR_FEATURE.START_AR_FEATURE,
    targetUserId: targetId,
  }
  _.session.signal({
    data: JSON.stringify(params),
    to: null,
    type: SIGNAL.AR_FEATURE,
  })
}

/**
 * @BROADCATE
 * AR feature status
 * @param {String} type = remote.config.AR_FEATURE
 */
const arFeatureStop = () => {
  const params = {
    type: AR_FEATURE.STOP_AR_FEATURE,
  }
  _.session.signal({
    data: JSON.stringify(params),
    to: null,
    type: SIGNAL.AR_FEATURE,
  })
}

/**
 * @TARGET
 * AR pointing
 * @param {String} type = remote.config.AR_POINTING
 * @param {Object} params (문서참조)
 */
const arPointing = (type, params = {}, target = null) => {
  params.type = type
  _.session.signal({
    data: JSON.stringify(params),
    to: target,
    type: SIGNAL.AR_POINTING,
  })
}

/**
 * AR 3D Content share
 * @param {String} type remote.config.AR_3D
 * @param {*} params
 * @param {*} target
 */
const ar3dContentShare = (type, params = {}, target = null) => {
  if (!_.session) return

  params.type = type
  _.session.signal({
    data: JSON.stringify(params),
    to: target,
    type: SIGNAL.AR_3D,
  })
}

/**
 * @TARGET
 * request screen capture permission
 * @param {Object} params
 */
const capturePermission = (target = null) => {
  const params = {
    type: 'request',
  }
  _.session.signal({
    type: SIGNAL.CAPTURE_PERMISSION,
    to: target,
    data: JSON.stringify(params),
  })
}

/**
 * @TARGET
 * AR drawing
 * @param {String} type = remote.config.AR_DRAWING
 * @param {Object} params (문서참조)
 */
const arDrawing = (type, params = {}, target = null) => {
  if (!_.session) return
  params.type = type
  _.session.signal({
    type: SIGNAL.AR_DRAWING,
    to: target,
    data: JSON.stringify(params),
  })
}

/**
 * @BROADCATE
 * @TARGET
 * my video stream control
 * @param {Boolean, String} status CAMERA_STATUS
 * @param {Boolean} publish video publish 여부. 기본값 true
 */
const camera = (
  status = CAMERA_STATUS.CAMERA_NONE,
  target = null,
  publish = true,
) => {
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
}

/**
 * @BROADCATE
 * @TARGET
 * my mic control
 * @param {Boolean} active
 */
const mic = (active, target = null) => {
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
}

/**
 * @BROADCATE
 * @TARGET
 * my speaker control
 * @param {Boolean} active
 */
const speaker = (active, target = null) => {
  for (let subscriber of _.subscribers) {
    if (subscriber.stream && subscriber.stream.mediaStream) {
      subscriber.subscribeToAudio(active)
    }
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
}

/**
 * @BROADCATE
 * @TARGET
 * other user's flash control
 * @param {Boolean} active
 */
const flashStatus = (status = FLASH_STATUS.FLASH_NONE, target = null) => {
  const params = {
    status: status,
    type: FLASH.STATUS,
  }
  _.session.signal({
    data: JSON.stringify(params),
    to: target,
    type: SIGNAL.FLASH,
  })
}

/**
 * @BROADCATE
 * @TARGET
 * other user's flash control
 * @param {Boolean} active
 * @param {String} id : target id
 */
const flash = (active, target) => {
  const params = {
    enable: active,
    type: FLASH.FLASH,
  }
  _.session.signal({
    data: JSON.stringify(params),
    to: target,
    type: SIGNAL.FLASH,
  })
}

/**
 * @BROADCATE
 * @TARGET
 * other user's camera control
 * @param {Boolean} active
 */
const cameraZoom = (level, target) => {
  const params = {
    type: CAMERA.ZOOM,
    level: level,
  }
  _.session.signal({
    data: JSON.stringify(params),
    to: target,
    type: SIGNAL.CAMERA,
  })
}

/**
 * 화면 공유 여부
 * @param {Boolean} enable 화면 공유 기능 사용 여부 true, false
 * @param {Array[String]} target 신호를 보낼 대상 커넥션 id String 배열
 */
const screenSharing = (enable, target = null) => {
  const params = {
    type: VIDEO.SCREEN_SHARE,
    enable: enable,
  }
  _.session.signal({
    type: SIGNAL.VIDEO,
    to: target,
    data: JSON.stringify(params),
  })
}

/**
 * 모바일 웹 유저가 백그라운드 상태가 되었을 때, 안드로이드 앱 유저에게
 * 백그라운드가 되었다고 알려주는 signal
 *
 * @BROADCATE
 * @param {Boolean} enable
 * @param {String} id : target id
 */
const backgroundStatus = (enable, target) => {
  const params = {
    type: APP.BACKGROUND,
    isBackground: enable,
  }
  _.session.signal({
    type: SIGNAL.APP,
    to: target,
    data: JSON.stringify(params),
  })
}

export default {
  chat,
  file,
  resolution,
  video,
  pointing,
  drawing,
  control,
  controlRestrict,
  arFeatureStart,
  arFeatureStop,
  arPointing,
  ar3dContentShare,
  capturePermission,
  arDrawing,
  camera,
  mic,
  speaker,
  flashStatus,
  flash,
  cameraZoom,
  screenSharing,
  backgroundStatus,
}
