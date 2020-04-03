import Profile from '@/models/profile/Profile'

import switchPromise from '@/test/switchPromise'

export default {
  getMyProfile() {
    return new Profile()
  },
  async certification(form) {
    await switchPromise()
    $nuxt.$store.commit('auth/SET_AUTH', true)
  },
  async changeMyImage(form) {
    await switchPromise()
  },
}
