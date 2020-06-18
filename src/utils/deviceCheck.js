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
      await navigator.mediaDevices.getUserMedia({
        audio: true,
        video: true,
      })
    }
    return true
  } catch (err) {
    console.error(err)
  }
}
