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
  join: async (roomInfo, account, users) => {
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
        serverData: users,
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
        publishVideo: true, // Whether you want to start publishing with your video enabled or not
        resolution: '1280x720', // The resolution of your video
        frameRate: 30, // The frame rate of your video
        insertMode: 'PREPEND', // How the video is inserted in the target element 'video-container'
        mirror: false, // Whether to mirror your local video or not
      })
      _.publisher.on('streamCreated', event => {
        Store.commit('addStream', getUserObject(_.publisher.stream))
        _.mic(Store.getters['mic'])
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
      Store.commit('clearStreams')
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
  pointing: message => {
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
  },
  mute: (id, statue) => {
    let idx = _.subscribers.findIndex(
      subscriber => subscriber.id.indexOf(id) > -1,
    )
    if (idx < 0) {
      console.log('can not find user')
      return
    }
    _.subscribers[idx].subscribeToAudio(statue)
  },
  disconnect: id => {},
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
