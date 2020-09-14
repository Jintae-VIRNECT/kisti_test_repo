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
