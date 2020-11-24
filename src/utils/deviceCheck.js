import Store from 'stores/remote/store'

/**
 * 권한 허용 체크 로직
 * @return {Object} { audioSource: Boolean, videoSource: Boolean }
 * @throw
 */
export const checkPermission = async (checkVideo = true) => {
  const devices = await navigator.mediaDevices.enumerateDevices()
  const hasVideo = checkVideo
    ? devices.findIndex(device => device.kind.toLowerCase() === 'videoinput') >
      -1
    : false
  // const hasVideo = false
  const hasAudio =
    devices.findIndex(device => device.kind.toLowerCase() === 'audioinput') > -1

  if (!hasAudio && !hasVideo) {
    throw 'nodevice'
  }
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
  if (permission === 'prompt') {
    const mediaResponse = await getUserMedia(true, hasVideo)
    if (typeof mediaResponse !== 'object') {
      throw mediaResponse
    }
  } else if (permission !== true) {
    throw permission
  }
  const options = {
    audioSource,
    videoSource,
  }
  return options
}

export const getPermission = async () => {
  try {
    if (!('permission' in navigator)) {
      return 'prompt'
    }

    const result = await Promise.all([
      navigator.permissions.query({ name: 'camera' }),
      navigator.permissions.query({ name: 'microphone' }),
    ])

    const [cameraState, micState] = result

    if (cameraState.state === 'denied' || micState.state === 'denied') {
      throw 'device access deined'
    }

    if (cameraState.state === 'prompt' || micState.state === 'prompt') {
      return 'prompt'
    }
    return true
  } catch (err) {
    if (typeof err === 'object') {
      return err.message
    }
    return err
  }
}

export const getUserMedia = async (audio, video) => {
  try {
    return await navigator.mediaDevices.getUserMedia({ audio, video })
  } catch (err) {
    throw err
  }
}

/**
 * camera 디바이스 여부 체크
 */
export const checkVideoInput = async () => {
  const devices = await navigator.mediaDevices.enumerateDevices()
  const idx = devices.findIndex(device => device.kind === 'videoinput')
  if (idx < 0) {
    return false
  }
  return true
}
