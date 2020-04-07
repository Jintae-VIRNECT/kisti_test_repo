import Profile from '@/models/profile/Profile'
import api from '@/api/gateway'

import switchPromise from '@/test/switchPromise'

export default {
  getMyProfile() {
    const profile = $nuxt.$store.getters['auth/myProfile']
    return new Profile(profile)
  },
  async certification(form) {
    const data = await api('ACCESS_AUTH', {
      route: { userId: form.uuid },
      params: form,
    })
    $nuxt.$store.commit('auth/SET_MY_PROFILE', data.userInfo)
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
