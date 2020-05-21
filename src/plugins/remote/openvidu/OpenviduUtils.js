export const addSession = stream => {
  let streamObj
  if (stream.nodeId === 'main') {
    streamObj = {
      stream: null,
      // session: session,
      nickName: stream.nickName,
      userName: stream.userName,
      nodeId: 'main',
    }
  } else {
    let connection = stream.connection

    let clientData = JSON.parse(connection.data.split('%/%')[0]).clientData,
      // serverData = JSON.parse(connection.data.split('%/%')[1]).serverData,
      nodeId = connection.connectionId

    streamObj = {
      stream: null,
      nickName: clientData,
      // userName: serverData,
      nodeId: nodeId,
      audio: stream.audioActive,
      video: stream.videoActive,
    }
  }

  // Store.dispatch('addSession', streamObj)
  return streamObj
}

export const getStream = async constraints => {
  if (!navigator.mediaDevices.getUserMedia) {
    console.error('getUserMedia 없음')
    return
  }
  try {
    const mediaStream = await navigator.mediaDevices.getUserMedia(constraints)

    const streamTracks = {}
    streamTracks.audioSource =
      mediaStream.getAudioTracks().length > 0
        ? mediaStream.getAudioTracks()[0]
        : null
    streamTracks.videoSource =
      mediaStream.getVideoTracks().length > 0
        ? mediaStream.getVideoTracks()[0]
        : null
    return streamTracks
  } catch (err) {
    console.error(err)
  }
}
