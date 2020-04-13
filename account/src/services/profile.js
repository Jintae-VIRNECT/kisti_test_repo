import Profile from '@/models/profile/Profile'
import api from '@/api/gateway'

function getMyProfile() {
  return process.client && { ...$nuxt.$store.getters['auth/myProfile'] }
}

export default {
  getMyProfile,
  async auth() {
    if (Object.keys(getMyProfile()).length) return false
    const data = await api('GET_AUTH_INFO')
    $nuxt.$store.commit('auth/SET_MY_PROFILE', new Profile(data.userInfo))
  },
  async certification(form) {
    await api('ACCESS_AUTH', {
      route: { userId: form.uuid },
      params: form,
    })
    $nuxt.$store.commit('auth/SET_AUTH', true)
  },
  async updateMyProfile(form) {
    await api('UPDATE_USER_INFO', {
      route: { userId: getMyProfile().uuid },
      params: form,
    })
  },
  async updateMyImage(form) {
    const formData = new FormData()
    formData.append('profile', form.profile ? form.profile : new File())
    formData.append('hasProfileImage', form.profile ? true : false)

    await api('UPDATE_USER_IMAGE', {
      route: { userId: getMyProfile().uuid },
      params: formData,
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
  },
}
