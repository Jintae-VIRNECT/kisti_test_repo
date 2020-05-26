import { OpenVidu } from './openvidu-browser'
import openviduStore from './OpenviduStore'
import { addSessionEventListener } from './OpenviduUtils'
import { getToken } from 'api/workspace/call'

export default {
  install(Vue, { Store }) {
    if (!Store) {
      throw new Error('Can not find vuex store')
    } else {
      Store.registerModule('openvidu', openviduStore)
    }
    let OV

    const setStream = (uuid, stream) => {
      Store.commit('setStream', {
        uuid: uuid,
        stream: stream,
      })
    }

    Vue.prototype.$call = {
      session: null,
      join: async (roomInfo, account, users, screen) => {
        try {
          const params = {
            sessionId: roomInfo.sessionId,
            role: 'PUBLISHER',
            data: {},
            kurentoOptions: {},
          }
          const rtnValue = await getToken(params)

          OV = new OpenVidu()
          console.log(OV)
          this.session = OV.initSession()

          addSessionEventListener(this.session, Store)
          console.log('[session] add event listener')
          const metaData = {
            clientData: account.uuid,
            serverData: users,
          }
          console.log(JSON.stringify(metaData))
          const iceServer = [
            {
              url: 'stun:stun.l.google.com:19302',
            },
            // {
            //   url: 'turn:turn.virnectremote.com:3478?transport=udp',
            //   username: 'virnect',
            //   credential: 'virnect',
            // },
            // {
            //   url: 'turn:turn.virnectremote.com:3478?transport=tcp',
            //   username: 'virnect',
            //   credential: 'virnect',
            // },
          ]

          await this.session.connect(
            rtnValue.token,
            JSON.stringify(metaData),
            iceServer,
          )
          console.log('[session] connection success')

          const publisher = OV.initPublisher('', {
            audioSource: undefined, // The source of audio. If undefined default microphone
            videoSource: undefined, //screen ? 'screen' : undefined, // The source of video. If undefined default webcam
            publishAudio: true, // Whether you want to start publishing with your audio unmuted or not
            publishVideo: true, // Whether you want to start publishing with your video enabled or not
            resolution: '1280x720', // The resolution of your video
            frameRate: 30, // The frame rate of your video
            insertMode: 'PREPEND', // How the video is inserted in the target element 'video-container'
            mirror: false, // Whether to mirror your local video or not
          })
          publisher.on('streamCreated', event => {
            console.log('[session] publisher stream created success')
            setStream('main', publisher.stream.getMediaStream())

            Store.commit('setMainView', {
              stream: publisher.stream.getMediaStream(),
              // session: session,
              // connection: publisher.connection,
              nickname: account.nickname,
              name: account.name,
              id: account.uuid,
            })
          })
          console.log(publisher)
          console.log(publisher.stream)
          console.log(publisher.stream.streamId)

          this.session.publish(publisher)
          console.log('[session] init publisher success')
          return true
        } catch (err) {
          console.error(err)
          return false
        }
        // return session
      },
      leave: () => {
        try {
          Store.commit('clearStreams')
          this.session.disconnect()
          this.session = null
        } catch (err) {
          throw err
        }
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
