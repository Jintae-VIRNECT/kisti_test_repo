import { Client } from '@stomp/stompjs'
import { DESTINATION, KEY } from 'configs/push.config'
import { logger, debug } from 'utils/logger'
import { wsUri } from 'api/gateway/api'

const push = {
  _inited: false,
  _listeners: [],
  subscriber: event => {
    for (let listener of push._listeners) {
      listener['cb'](event)
    }
  },
  init: workspaceId => {
    return new Promise((resolve, reject) => {
      if (push._inited === true) {
        resolve()
        return
      }
      push._inited = true

      const config = {
        brokerURL: `${window.urls.wsapi}${wsUri['MESSAGE']}`,
        connectHeaders: {
          login: 'guest',
          passcode: 'guest',
        },
        debug: str => {
          debug('::message::', str)
        },
        reconnectDelay: 3 * 1000,
        heartbeatIncoming: 10 * 1000,
        heartbeatOutgoing: 10 * 1000,
      }

      const client = new Client(config)

      Object.assign(push, client)

      client.onConnect = frame => {
        logger('message', 'connected')
        debug('::message::', client, frame)
        client.subscribe(
          `${DESTINATION.PUSH}.${KEY.SERVICE_TYPE}.${workspaceId}`,
          push.subscriber,
        )
        resolve()
      }

      client.onStompError = frame => {
        console.error('Broker reported error: ' + frame.headers['message'])
        debug('Additional details: ' + frame.body)
        reject()
      }

      client.onDisconnect = frame => {
        logger('message', 'disconnected')
        debug('Additional details: ' + frame.body)
        reject()
      }

      // pushListener(client)

      client.activate()
    })
  },
  addListener: (key, cb) => {
    if (typeof cb !== 'function') return
    push._listeners.push({
      key,
      cb,
    })
  },
  removeListener: key => {
    const idx = push._listeners.findIndex(listen => listen.key === key)
    if (idx < 0) return

    push._listeners.splice(idx, 1)
  },
}

export default {
  install(Vue) {
    Vue.prototype.$push = push
  },
}
