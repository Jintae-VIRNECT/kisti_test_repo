import { Client } from '@stomp/stompjs'
import { DESTINATION, KEY } from 'configs/push.config'
import { URLS } from 'configs/env.config'
import { logger, debug } from 'utils/logger'
import { wsUri } from 'api/gateway/api'

let client

const push = {
  _inited: false,
  _listeners: [],
  _subscription: null,
  _forceLogoutSubscription: null,
  subscriber: event => {
    for (let listener of push._listeners) {
      listener['cb'](event)
    }
  },
  init: workspace => {
    return new Promise((resolve, reject) => {
      if (push._inited === true) {
        resolve()
        return
      }
      push._inited = true

      const workspaceId = workspace.uuid

      const config = {
        brokerURL: `${URLS['ws']}${wsUri['MESSAGE']}`,
        connectHeaders: {
          login: 'guest',
          passcode: 'guest',
        },
        // debug: str => {
        //   debug('::message::', str)
        // },
        reconnectDelay: 3 * 1000,
        heartbeatIncoming: 10 * 1000,
        heartbeatOutgoing: 10 * 1000,
      }

      client = new Client(config)

      // Object.assign(push, client)

      client.onConnect = frame => {
        logger('message', 'connected')
        debug('::message::', client, frame)
        if (!workspace.expire) {
          debug(
            'message::subscribe::',
            `${DESTINATION.PUSH}.${KEY.SERVICE_TYPE}.${workspaceId}`,
          )
          push._subscription = client.subscribe(
            `${DESTINATION.PUSH}.${KEY.SERVICE_TYPE}.${workspaceId}`,
            push.subscriber,
          )
        }
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
  changeSubscribe: workspace => {
    if (push._inited === false) return
    logger('message', 'change subscribe')
    if (push._subscription) {
      debug('message::unsubscribe::', push._subscription)
      push._subscription.unsubscribe()
      push._subscription = null
    }
    const workspaceId = workspace.uuid
    if (workspace.expire) return

    debug(
      'message::subscribe::',
      `${DESTINATION.PUSH}.${KEY.SERVICE_TYPE}.${workspaceId}`,
    )

    push._subscription = client.subscribe(
      `${DESTINATION.PUSH}.${KEY.SERVICE_TYPE}.${workspaceId}`,
      push.subscriber,
    )
  },
  addListener: (key, cb) => {
    if (typeof cb !== 'function') return
    if (push._listeners.findIndex(listener => listener.key === key) > -1) return
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

  addForceLogoutSubscriber: (userId, cb) => {
    debug('message::subscribe::', `${DESTINATION.FORCELOGOUT}.${userId}`)
    push._forceLogoutSubscription = client.subscribe(
      `${DESTINATION.FORCELOGOUT}.${userId}`,
      cb,
    )
  },
  removeForceLogoutSubscriber: () => {
    if (push._forceLogoutSubscription) {
      debug('message::unsubscribe::', push._forceLogoutSubscription)
      push._forceLogoutSubscription.unsubscribe()
      push._forceLogoutSubscription = null
    }
  },
}

export default {
  install(Vue) {
    Vue.prototype.$push = push
  },
}
