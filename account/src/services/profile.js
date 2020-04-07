import Profile from '@/models/profile/Profile'
import api from '@/api/gateway'

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
  async updateMyProfile(form) {
    const data = await api('UPDATE_USER_INFO', {
      route: { userId: form.userId },
      params: form,
    })
    $nuxt.$store.commit('auth/SET_MY_PROFILE', data)
  },
}
