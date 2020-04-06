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
  async changeMyName(form) {
    await switchPromise()
  },
  async changeMyNickname(form) {
    await switchPromise()
  },
  async changeMyPassword(form) {
    await switchPromise()
  },
  async changeMyBirth(form) {
    await switchPromise()
  },
  async changeMyContact(form) {
    await switchPromise()
  },
}
