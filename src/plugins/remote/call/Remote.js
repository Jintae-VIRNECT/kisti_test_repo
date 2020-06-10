import { OpenVidu } from './openvidu'
import { addSessionEventListener, getUserObject } from './RemoteUtils'
import { getToken } from 'api/workspace/call'
import Store from 'stores/remote/store'

let OV

const _ = {
  session: null,
  publisher: null,
  subscribers: [],
  resolution: null,
  join: async (roomInfo, account, role) => {
    Store.commit('clear')
    try {
      const params = {
        sessionId: roomInfo.sessionId,
        role: 'PUBLISHER',
        data: {},
        kurentoOptions: {},
      }
      const rtnValue = await getToken(params)

      OV = new OpenVidu()
      _.session = OV.initSession()

      addSessionEventListener(_.session, Store)
      const metaData = {
        clientData: account.uuid,
        roleType: role,
      }

      const iceServer = [
        {
          url: 'turn:turn.virnectremote.com:3478?transport=udp',
          username: 'virnect',
          credential: 'virnect',
        },
        {
          url: 'turn:turn.virnectremote.com:3478?transport=tcp',
          username: 'virnect',
          credential: 'virnect',
        },
      ]

      await _.session.connect(
        rtnValue.token,
        JSON.stringify(metaData),
        iceServer,
      )

      _.publisher = OV.initPublisher('', {
        audioSource: undefined, // The source of audio. If undefined default microphone
        videoSource: undefined, //screen ? 'screen' : undefined, // The source of video. If undefined default webcam
        publishAudio: true, // Whether you want to start publishing with your audio unmuted or not
        publishVideo: role !== 'LEADER', // Whether you want to start publishing with your video enabled or not
        resolution: '1280x720', // The resolution of your video
        frameRate: 30, // The frame rate of your video
        insertMode: 'PREPEND', // How the video is inserted in the target element 'video-container'
        mirror: false, // Whether to mirror your local video or not
      })
      _.publisher.on('streamCreated', () => {
        Store.commit('addStream', getUserObject(_.publisher.stream))
        _.mic(Store.getters['mic'])
      })
      _.publisher.on('streamPropertyChanged', event => {
        console.log(event)
        // Store.commit('addStream', getUserObject(_.publisher.stream))
        // _.mic(Store.getters['mic'])
      })

      _.session.publish(_.publisher)
      return true
    } catch (err) {
      console.error(err)
      return false
    }
  },
  leave: () => {
    try {
      Store.commit('clear')
      _.session.disconnect()
      _.session = null
      _.publisher = null
    } catch (err) {
      throw err
    }
  },
  sendChat: text => {
    if (text.trim().length === 0) return
    _.session.signal({
      data: text.trim(),
      to: _.session.connection,
      type: 'signal:chat',
    })
  },
  sendResolution: resolution => {
    if (resolution) {
      _.resolution = resolution
    } else {
      resolution = _.resolution
    }
    if (!resolution || !resolution.width) return
    _.session.signal({
      data: JSON.stringify(resolution),
      to: _.session.connection,
      type: 'signal:resolution',
    })
  },
  sendMessage: (type, params) => {
    const account = Store.getters['account']
    params['from'] = account.uuid
    params['to'] = []
    _.session.signal({
      type: `signal:${type}`,
      to: _.session.connection,
      data: JSON.stringify(params),
    })
  },
  pointing: message => {
    console.log('send pointing: ', JSON.stringify(message))
    _.session.signal({
      data: JSON.stringify(message),
      to: _.session.connection,
      type: 'signal:pointing',
    })
  },
  getDevices: () => {},
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
  streamOnOff: active => {
    _.publisher.publishVideo(active)
  },
  mic: active => {
    if (!_.publisher) return
    _.publisher.publishAudio(active)
    _.session.signal({
      data: active ? 'true' : 'false',
      to: _.session.connection,
      type: 'signal:mic',
    })
  },
  speaker: active => {
    for (let subscriber of _.subscribers) {
      subscriber.subscribeToAudio(active)
    }
    _.session.signal({
      data: active ? 'true' : 'false',
      to: _.session.connection,
      type: 'signal:audio',
    })
  },
  mute: (connectionId, mute) => {
    let idx = _.subscribers.findIndex(
      subscriber => subscriber.stream.connection.connectionId === connectionId,
    )
    if (idx < 0) {
      console.log('can not find user')
      return
    }
    _.subscribers[idx].subscribeToAudio(!mute)
    Store.commit('propertyChanged', {
      connectionId: connectionId,
      mute: mute,
    })
  },
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
  record: () => {},
  stop: () => {},
  active: () => {},
  /**
   * append message channel listener
   * @param {Function} customFunc
   */
  addListener: (type, func) => {
    _.session.on(type, func)
  },
  removeListener: (type, func) => {
    // _.session.off(type, func)
  },
}

export const addSubscriber = subscriber => {
  console.log(subscriber)
  _.subscribers.push(subscriber)
}

export const removeSubscriber = streamId => {
  const idx = _.subscribers.findIndex(user => user.stream.streamId === streamId)
  if (idx < 0) return
  _.subscribers.splice(idx, 1)
}

export default _
