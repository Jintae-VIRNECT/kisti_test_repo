import Store from 'stores/remote/store'

export const checkPermission = async () => {
  const devices = await navigator.mediaDevices.enumerateDevices()
  const hasVideo =
    devices.findIndex(device => device.kind.toLowerCase() === 'videoinput') > -1
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
    if (mediaResponse !== true) {
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
    await navigator.mediaDevices.getUserMedia({ audio, video })
    return true
  } catch (err) {
    if (typeof err === 'object') {
      if (err.name && err.name.toLowerCase() === 'notallowederror') {
        return 'device access deined'
      }
      return err.name
    }
    return err
  }
}
