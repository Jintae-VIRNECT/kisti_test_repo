import Store from 'stores/remote/store'

export const addSessionEventListener = session => {
  session.on('streamCreated', event => {
    console.log('[session] stream created')

    // let nodeId = event.stream.connection.connectionId
    const subscriber = session.subscribe(event.stream, '', () => {
      console.log(event.stream.mediaStream)
      // streamObj.stream = subscriber.stream.mediaStream
      // $store.commit('setStream', streamObj)
      const streamObj = getUserObject(subscriber.stream)
      console.log(streamObj)
      Store.commit('addStream', streamObj)
    })
    console.log(subscriber)
    subscriber.on('streamPlaying', e => {
      console.log('[subscriber] stream playing')
      console.log(e)
    })
  })

  // On every Stream destroyed...
  session.on('streamDestroyed', event => {
    console.log('[session] stream destroyed')
    const connectionId = event.stream.connection.connectionId
    Store.commit('removeStream', connectionId)
  })

  session.on('signal:audio', event => {
    console.log(event)
  })

  session.on('signal:video', event => {
    console.log(event)
  })

  session.on('signal:chat', event => {
    console.log(event)
  })
}

export const getUserObject = stream => {
  const participants = Store.getters['roomParticipants']
  console.log(participants)
  let streamObj
  let connection = stream.connection

  let uuid = JSON.parse(connection.data.split('%/%')[0]).clientData

  const participant = participants.find(user => {
    console.log(user)
    console.log(uuid)
    return user.uuid === uuid
  })

  streamObj = {
    id: uuid,
    stream: stream.mediaStream,
    connectionId: stream.connection.connectionId,
    nickname: participant.nickname,
    path: participant.path,
    audio: stream.audioActive,
    video: stream.videoActive,
  }

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

const setStream = (streamObj, subscriber, idx) => {
  console.log('[subscriber] finding stream....')
  if (idx > 10) {
    console.log('스트림을 못찾았습니다.')
    return
  }
  setTimeout(() => {
    if (!subscriber.stream.mediaStream) {
      setStream(streamObj, subscriber, idx++)
    } else {
      streamObj.stream = subscriber.stream.mediaStream
      Store.commit('setStream', streamObj)
    }
  }, 1000)
}
