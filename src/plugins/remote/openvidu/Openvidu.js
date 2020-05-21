import { OpenVidu } from './openvidu-browser'
import openviduStore from './OpenviduStore'
import { getStream } from './OpenviduUtils'
import { getToken } from 'api/workspace/call'

export default {
  install(Vue, { Store }) {
    if (!Store) {
      throw new Error('Can not find vuex store')
    } else {
      Store.registerModule('openvidu', openviduStore)
    }
    let OV

    const addSessionEventListener = session => {
      session.on('streamCreated', event => {
        console.log(event)
      })

      // On every Stream destroyed...
      session.on('streamDestroyed', event => {
        console.log(event)
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

    const updateSessionStream = (nodeId, stream) => {
      Store.dispatch('updateSessionStream', {
        nodeId: nodeId,
        stream: stream,
      })
    }

    Vue.prototype.$call = {
      session: null,
      join: async (roomInfo, account) => {
        try {
          const params = {
            sessionId: roomInfo.sessionId,
            role: 'PUBLISHER',
            data: {},
            kurentoOptions: {},
          }
          const rtnValue = await getToken(params)

          OV = new OpenVidu()
          this.session = OV.initSession()

          addSessionEventListener(this.session)
          console.log('[session] add event listener')

          await this.session.connect(rtnValue.token, {
            clientData: account.nickname,
          })
          console.log('[session] connection success')
          // const streams = await getStream({
          //   video: true,
          //   audio: true,
          // })
          // console.log(streams)

          const publisher = OV.initPublisher('', {
            audioSource: undefined, // The source of audio. If undefined default microphone
            videoSource: undefined, // The source of video. If undefined default webcam
            publishAudio: true, // Whether you want to start publishing with your audio unmuted or not
            publishVideo: true, // Whether you want to start publishing with your video enabled or not
            resolution: '1280x720', // The resolution of your video
            frameRate: 30, // The frame rate of your video
            insertMode: 'PREPEND', // How the video is inserted in the target element 'video-container'
            mirror: false, // Whether to mirror your local video or not
          })
          publisher.on('streamCreated', event => {
            console.log('[session] publisher stream created success')
            updateSessionStream('main', publisher.stream.getMediaStream())

            Store.dispatch('setMainSession', {
              stream: publisher.stream.getMediaStream(),
              // session: session,
              // connection: publisher.connection,
              nickName: account.nickname,
              userName: account.name,
              nodeId: 'main',
            })
            console.log()
          })

          this.session.publish(publisher)
          console.log(publisher)
          console.log('[session] init publisher success')
          return true
        } catch (err) {
          console.error(err)
          return false
        }
        // return session
      },
      leave: () => {
        Store.dispatch('clearSession')
        this.session.disconnect()
        this.session = null
      },
      sendChat: text => {},
      getDevices: () => {},
      getState: () => {},
      streamOnOff: active => {},
      micOnOff: active => {},
      audioOnOff: (id, statue) => {},
      disconnect: id => {},
      record: () => {},
      stop: () => {},
      active: () => {},
    }
  },
}
