import { context } from '@/plugins/context'
import auth from 'WC-Modules/javascript/api/virnectPlatform/virnectPlatformAuth'

export default {
  state: () => ({
    auth: {
      env: '',
      urls: {},
      myInfo: {},
    },
  }),
  getters: {
    auth(state) {
      return state.auth
    },
  },
  mutations: {
    SET_AUTH(state, obj) {
      state.auth = obj
    },
  },
  actions: {
    async getAuth({ commit }) {
      commit(
        'SET_AUTH',
        await auth.init({
          env: context.$config.VIRNECT_ENV,
          urls: context.$url,
          timeout: context.$config.API_TIMEOUT,
        }),
      )
    },
  },
}
