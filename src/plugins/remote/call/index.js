import remoteStore from './RemoteStore'
import Remote from './Remote'

export default {
  install(Vue, { Store }) {
    if (!Store) {
      throw new Error('Can not find vuex store')
    } else {
      Store.registerModule('remote', remoteStore)
    }

    Vue.prototype.$call = Remote
  },
}
