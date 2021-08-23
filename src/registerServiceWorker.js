/* eslint-disable no-console */

import { register } from 'register-service-worker'

// if (process.env.NODE_ENV === 'production') {
register(`sw.js`, {
  ready() {
    console.log('Remote Web App is Ready')
  },
  registered() {
    console.log('Remote Web Service worker has been registered.')
  },
  cached() {
    console.log('Remote Web Content has been cached for offline use.')
  },
  updatefound() {
    console.log('Remote Web New content is downloading.')
  },
  updated() {
    console.log('Remote Web New content is available; please refresh.')
  },
  offline() {
    console.log(
      'Remote Web No internet connection found. App is running in offline mode.',
    )
  },
  error(error) {
    console.error('Remote Web Error during service worker registration:', error)
  },
})
// }
