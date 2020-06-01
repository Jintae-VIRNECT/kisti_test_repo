import Store from 'stores/remote/store'
import { addSubscriber, removeSubscriber } from './Remote'

export const addSessionEventListener = session => {
  session.on('streamCreated', event => {
    console.log('[session] stream created')

    const subscriber = session.subscribe(event.stream, '', () => {
      const streamObj = getUserObject(subscriber.stream)
      Store.commit('addStream', streamObj)
    })
    addSubscriber(subscriber)
    // subscriber.on('streamPlaying', e => {
    //   console.log('[subscriber] stream playing')
    // })
  })

  session.on('streamDestroyed', event => {
    console.log('[session] stream destroyed')
    const connectionId = event.stream.connection.connectionId
    Store.commit('removeStream', connectionId)
    removeSubscriber(event.stream.streamId)
  })

  session.on('signal:audio', event => {
    console.log(event)
  })

  session.on('signal:video', event => {
    console.log(event)
  })

  session.on('signal:chat', event => {
    console.log(event)
    let data = event.data
    let chat = {
      text: data.replace(/\</g, '&lt;'),
      name: JSON.parse(event.from.data.split('%/%')[0]).clientData,
      date: new Date(),
      nodeId: event.from.connectionId,
      type: false,
    }
    if (session.connection.connectionId === event.from.connectionId) {
      // 본인
      chat.type = 'me'
    }
    Store.commit('addChat', chat)
  })
}

export const getUserObject = stream => {
  const participants = Store.getters['roomParticipants']
  let streamObj
  let connection = stream.connection

  let uuid = JSON.parse(connection.data.split('%/%')[0]).clientData

  const participant = participants.find(user => {
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
