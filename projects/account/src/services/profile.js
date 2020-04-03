import Profile from '@/models/profile/Profile'

import randomPromise from '@/test/randomPromise'

export default {
  getMyProfile() {
    return new Profile()
  },
  async certification(form) {
    if (await randomPromise()) {
      $nuxt.$store.commit('auth/SET_AUTH', true)
      return true
    }
  },
}
