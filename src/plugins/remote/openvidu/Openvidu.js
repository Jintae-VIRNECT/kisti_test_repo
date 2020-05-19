import { OpenVidu } from 'openvidu-browser'
import openviduStore from './OpenviduStore'
import { getToken } from 'api/workspace/call'
import io from 'socket.io-client'

export default {
  install(Vue, { Store }) {
    console.log(Store)

    if (!Store) {
      throw new Error('Can not find vuex store')
    } else {
      Store.registerModule('openvidu', openviduStore)
    }
    let OV

    const addSessionEventListener = session => {
      session.on('streamCreated', event => {})

      // On every Stream destroyed...
      session.on('streamDestroyed', event => {})

      session.on('signal:audio', event => {})

      session.on('signal:video', event => {})

      session.on('signal:chat', event => {})
    }

    Vue.prototype.$call = {
      join: async (roomInfo, nickname) => {
        try {
          const params = {
            sessionId: roomInfo.sessionId,
            role: 'PUBLISHER',
            data: {},
            kurentoOptions: {},
          }
          const rtnValue = await getToken(params)
          console.log(rtnValue)

          OV = new OpenVidu()
          const session = OV.initSession()
          console.log(session)
          // const session = io(rtnValue.token)
          // console.log(session)
          addSessionEventListener(session)
          console.log('session add event')

          session.connect(rtnValue.token, { clientData: nickname }).then(() => {
            console.log('connection success')
          })
        } catch (err) {
          console.error(err)
        }
        // return session
      },
      leave: () => {},
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
