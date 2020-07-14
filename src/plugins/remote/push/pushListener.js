import { DESTINATION, KEY } from 'configs/push.config'

let push
let connInterval = null
const listeners = []

const connection = uuid => {
  if (!connInterval) {
    connInterval = setInterval(() => {
      if (push.connected) {
        push.subscribe(
          `${DESTINATION.PUSH}.${KEY.SERVICE_TYPE}.${uuid}`,
          subscribe,
        )
        clearInterval(connInterval)
      }
    }, 3000)
  }
}

const subscribe = event => {
  for (let listener of listeners) {
    listener['cb'](event)
  }
}

const addListener = (key, cb) => {
  if (typeof cb !== 'function') return
  listeners.push({
    key,
    cb,
  })
}

const removeListener = key => {
  const idx = listeners.findIndex(listen => listen.key === key)
  if (idx < 0) return

  listeners.splice(idx, 1)
}

const pushListener = client => {
  push = client
  push.connection = connection
  push.addListener = addListener
  push.removeListener = removeListener
}

export default pushListener
