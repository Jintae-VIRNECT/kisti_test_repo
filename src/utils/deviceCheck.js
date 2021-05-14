/**
 * 권한 허용 체크 로직
 * @param {Object} { video: Boolean, audio: Boolean }
 * @return {Object} { audioSource: Boolean, videoSource: Boolean }
 * @return {Boolean} false: no video, no audio device
 * @throw
 */
export const checkPermission = async ({ video = true, audio = true }) => {
  let { hasVideo, hasAudio, devices } = await checkInput({
    video,
    audio,
  })
  console.log(`Device Info :: video :: ${hasVideo}`)
  console.log(`Device Info :: audio :: ${hasAudio}`)

  // if (!hasAudio && !hasVideo) {
  //   // throw 'nodevice'
  //   return { hasVideo, hasAudio, devices }
  // }
  const permission = await getPermission({
    camera: hasVideo,
    mic: hasAudio,
  })
  if (permission === 'prompt') {
    const mediaResponse = await getUserMedia({
      video: hasVideo,
      audio: hasAudio,
    })
    if (typeof mediaResponse !== 'object') {
      throw mediaResponse
    }
    const input = await checkInput({
      video,
      audio,
    })
    devices = input.devices
  } else if (permission !== true) {
    throw permission
  }
  return { hasVideo, hasAudio, devices }
}

/**
 * 입력 디바이스 아이디
 * @param {Object} 가져올 디바이스 { video, audio }
 * @param {Object} 기본 저장값 { camera, audio }
 */
export const getInputDevice = async (
  { video = true, audio = true },
  { camera = undefined, mic = undefined },
) => {
  const { hasVideo, hasAudio, devices } = await checkPermission({
    video,
    audio,
  })
  // if (!hasVideo && !hasAudio) return false
  // const settingInfo = Store.getters['settingInfo']
  let videoSource = hasVideo
    ? devices.findIndex(device => device.deviceId === camera) > -1
      ? camera
      : undefined
    : false
  let audioSource = hasAudio
    ? devices.findIndex(device => device.deviceId === mic) > -1
      ? mic
      : undefined
    : false

  const options = {
    audioSource,
    videoSource,
  }
  return options
}

/**
 * 현재 권한 체크
 * @return {Object} { camera: Boolean, mic: Boolean }
 * @return {Boolean} false: no video, no audio device
 * @throw
 */
export const getPermission = async ({ camera = true, mic = true }) => {
  try {
    if (!('permissions' in navigator)) {
      return 'prompt'
    }

    const result = await Promise.all([
      navigator.permissions.query({ name: 'camera' }),
      navigator.permissions.query({ name: 'microphone' }),
    ])

    const [cameraState, micState] = result

    if (
      (camera && cameraState.state === 'denied') ||
      (mic && micState.state === 'denied')
    ) {
      throw 'device access deined'
    }

    if (
      (camera && cameraState.state === 'prompt') ||
      (mic && micState.state === 'prompt')
    ) {
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

export const getUserMedia = async ({ video = true, audio = true }) => {
  try {
    return await navigator.mediaDevices.getUserMedia({ video, audio })
  } catch (err) {
    throw err
  }
}

/**
 * Input 디바이스 여부 체크
 * @param {Object} { video, audio }
 * @return {Object} { hasVideo, hasAudio }
 */
export const checkInput = async ({ video = true, audio = false }) => {
  if (!video && !audio) throw 'Please set true any device'
  const devices = await navigator.mediaDevices.enumerateDevices()
  const checked = {
    hasVideo: false,
    hasAudio: false,
    devices,
  }
  if (video) {
    const idx = devices.findIndex(device => device.kind === 'videoinput')
    if (idx > -1) {
      checked.hasVideo = true
    }
  }
  if (audio) {
    const idx = devices.findIndex(device => device.kind === 'audioinput')
    if (idx > -1) {
      checked.hasAudio = true
    }
  }
  return checked
}
